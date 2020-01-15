package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static com.space.controller.ShipSpecification.*;

@Service
public class ShipServiceImpl implements ShipService {

    private ShipRepository shipRepository;

    @PersistenceContext
    private EntityManager em;

    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public List<Ship> selectWithFilter(Specification<Ship> specification, Pageable pageable) {
        return  shipRepository.findAll(specification, pageable).getContent();
    }

    private List<Ship> filterDates(List<Ship> selectedShipsWithAllDates, int afterYear, int beforeYear) {

        List<Ship> ships = new ArrayList<>();
        for (Ship ship : selectedShipsWithAllDates) {
            int year = getYearFromTimestamp(ship.getProdDate().getTime());

            if (
                year >= afterYear &&
                year < beforeYear
            ) {
                    ships.add(ship);
            }
        }

        return ships;

    }

    private List<String> getSelectedShipTypes(ShipType shipType) {
        List<String> shipTypes = new ArrayList<>();
        if(shipType == null){
            shipTypes.add(ShipType.MILITARY.name());
            shipTypes.add(ShipType.MERCHANT.name());
            shipTypes.add(ShipType.TRANSPORT.name());
        } else {
            String shipTypeName = shipType.name();
            shipTypes.add(shipTypeName);
        }
        return shipTypes;
    }

    private int getYearFromTimestamp(Long after) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(after);
        return  calendar.get(Calendar.YEAR);
    }

    public List<Ship> findAll(){
        return shipRepository.findAll();
    }

    @Override
    public boolean isExistShipById(long id) {
        return shipRepository.existsById(id);
    }

    @Override
    public Specification<Ship> filterByName(String name) {

        if(name == null ) name = "";

        return Specification.where(paramContains("name", name));
    }

    @Override
    public Specification<Ship> filterByPlanet(String planet) {

        if(planet == null) planet = "";

        return paramContains("planet", planet);
    }

    @Override
    public Specification<Ship> filterByShipType(ShipType shipType) {
        if(shipType == null) return null;

        return paramEquals("shipType", shipType.name());
    }

    @Override
    public Specification<Ship> filterByProdDate(Long after, Long before) {
        if(after == null && before == null) return null;

        String prodDateName = "prodDate";
        if(before == null){
            Date afterDate = getDateFromMillis(after);
            return dateGreaterEqual(prodDateName, afterDate);
        }

        if(after == null){
            Date beforeDate = getDateFromMillis(before);
            return dateLessEqual(prodDateName, beforeDate);
        }

        Date afterDate = getDateFromMillis(after);
        Date beforeDate = getDateFromMillis(before);

        return dateGreaterEqual(prodDateName, afterDate).and(
                dateLessEqual(prodDateName, beforeDate));
    }

    @Override
    public Specification<Ship> filterByUsed(Boolean isUsed) {
        if(isUsed == null) return null;

        return paramEquals("isUsed", isUsed);
    }

    @Override
    public Specification<Ship> filterBySpeed(Double minSpeed, Double maxSpeed) {
        if(minSpeed == null && maxSpeed == null) return null;

        String speedName = "speed";

        if(minSpeed == null){
            return paramLessEqual(speedName, maxSpeed);
        }

        if (maxSpeed == null){
            return paramGreaterEqual(speedName, minSpeed);
        }

        return paramLessEqual(speedName, maxSpeed).and(
            paramGreaterEqual(speedName, minSpeed)
        );
    }

    @Override
    public Specification<Ship> filterByCrewSize(Integer minCrewSize, Integer maxCrewSize) {
        if (minCrewSize == null && maxCrewSize == null) return null;

        String crewSizeName = "crewSize";

        if(minCrewSize == null) return paramLessEqual(crewSizeName, maxCrewSize);

        if(maxCrewSize == null) return paramGreaterEqual(crewSizeName, minCrewSize);

        return paramGreaterEqual(crewSizeName, minCrewSize).and(
                paramLessEqual(crewSizeName, maxCrewSize)
        );
    }

    private Date getDateFromMillis(Long millisecs) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecs);
        return calendar.getTime();
    }

    @Override
    public Specification<Ship> filterByRating(Double minRating, Double maxRating) {
        if(minRating == null && maxRating == null) return null;

        String ratingName = "rating";
        if(minRating == null) return paramLessEqual(ratingName, maxRating);

        if(maxRating == null) return paramGreaterEqual(ratingName, minRating);

        return paramLessEqual(ratingName, maxRating).and(
                paramGreaterEqual(ratingName, minRating)
        );
    }

    @Override
    public Map<String, Object> getShipLimits() {
        Map<String, Object> limits = new HashMap<>();

        limits.put("prodYear", getPropLimits(Ship.ProdDate.MIN, Ship.ProdDate.MAX));

        limits.put("crewSize", getPropLimits(Ship.CrewSize.MIN, Ship.CrewSize.MAX));

        limits.put("speed", getPropLimits(Double.valueOf(Ship.Speed.MIN), Double.valueOf(Ship.Speed.MAX)));

        return limits;
    }

    @Override
    public Ship getShipById(Long id) {
        return shipRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteShipById(long id) {
        shipRepository.deleteById(id);
    }

    @Override
    public void save(Ship ship) {

        if(ship.isUsed() == null) ship.setIsUsed(false);
        ship.setRating();

        shipRepository.save(ship);
    }

    @Override
    public long getCount(Specification<Ship> specification) {
        return shipRepository.count(specification);
    }

    private Map<String, Number> getPropLimits(Number min, Number max) {
        Map<String, Number> propLimits = new HashMap<>();

        propLimits.put("min", min);
        propLimits.put("max", max);

        return propLimits;
    }
}
