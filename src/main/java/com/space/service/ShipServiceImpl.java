package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShipServiceImpl implements ShipService {

    private ShipRepository shipRepository;

    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Collection<Ship> selectShips(
        String name, String planet,
        ShipType shipType,
        Long after, Long before,
        Boolean isUsed, Boolean isNew,
        Double minSpeed, Double maxSpeed,
        Integer minCrewSize, Integer maxCrewSize,
        Double minRating, Double maxRating,

        Integer pageNumber, Integer pageSize,

        ShipOrder order)
    {

        List<String> shipTypes = getSelectedShipTypes(shipType);

        minSpeed = minSpeed == null ? 0 : minSpeed;
        maxSpeed = maxSpeed == null ? 99999 : maxSpeed;

        minRating = minRating == null ? 0 : minRating;
        maxRating = maxRating == null ? 99999 : maxRating;

        minCrewSize = minCrewSize == null ? Ship.CrewSize.MIN : minCrewSize;
        maxCrewSize = maxCrewSize == null ? Ship.CrewSize.MAX : maxCrewSize;

        pageNumber = pageNumber == null ? 0 : pageNumber;
        pageSize = pageSize == null || pageSize == 0  ? 99999 : pageSize;

        String sortOrder = order == null ? ShipOrder.ID.getFieldName() : order.getFieldName();

        List<Ship> selectedShipsWithAllDates = shipRepository.selectShips(
                name,
                planet,
                shipTypes,
                isUsed, isNew,

                minSpeed, maxSpeed,

                minCrewSize, maxCrewSize,
                minRating, maxRating,

                Sort.by(Sort.Direction.ASC, sortOrder)
        );

        List<Ship> selectedShips = filterDates(selectedShipsWithAllDates, after, before);

        return selectedShips.stream()
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    private List<Ship> filterDates(List<Ship> selectedShipsWithAllDates, Long after, Long before) {

//        if(after == null && before == null){
//            return selectedShipsWithAllDates;
//        }
//
//        List<Ship> ships = new ArrayList<>();
//
//        if (after == null) {
//            Calendar beforeCalendar = getCalendarDate(before);
//
//
//            for (Ship ship : selectedShipsWithAllDates) {
//                Calendar productionDate = getCalendarDate(ship.getProdDate());
//
//                if((productionDate.get(Calendar.YEAR) - beforeCalendar.get(Calendar.YEAR)) < 0){
//                    ships.add(ship);
//                }
//            }
//
//            return ships;
//        }
//
//        if (before == null) {
//            Calendar afterCalendar = getCalendarDate(after);
//
//            for (Ship ship : selectedShipsWithAllDates) {
//                Calendar productionDate = getCalendarDate(ship.getProdDate());
//
//                if((productionDate.get(Calendar.YEAR) -  afterCalendar.get(Calendar.YEAR)) >= 0){
//                    ships.add(ship);
//                }
//            }
//
//            return ships;
//        }
//
//        Calendar afterCalendar = getCalendarDate(after);
//
//        Calendar beforeCalendar = getCalendarDate(before);
//
//
//        for (Ship ship : selectedShipsWithAllDates) {
//            Calendar productionDate = getCalendarDate(ship.getProdDate());
//
//            if((productionDate.get(Calendar.YEAR) -  afterCalendar.get(Calendar.YEAR)) >= 0 &&
//                (productionDate.get(Calendar.YEAR) - beforeCalendar.get(Calendar.YEAR)) < 0)
//            {
//                ships.add(ship);
//            }
//        }
//
//        return ships;

        if(after != null) {
            selectedShipsWithAllDates = selectedShipsWithAllDates.stream().filter(ship -> ship.getProdDate().after(new Date(after))).collect(Collectors.toList());
        }

        if(before != null) {
            selectedShipsWithAllDates = selectedShipsWithAllDates.stream().filter(ship -> ship.getProdDate().before(new Date(before))).collect(Collectors.toList());
        }

        return selectedShipsWithAllDates;

    }

    private Calendar getCalendarDate(Long millisecs) {
        Calendar afterCalendar = Calendar.getInstance();
        afterCalendar.setTimeInMillis(millisecs);
        return afterCalendar;
    }

    private Calendar getCalendarDate(Date date) {
        Calendar prodactionDate = Calendar.getInstance();
        prodactionDate.setTime(date);
        return prodactionDate;
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

    public List<Ship> findAll(){
        return shipRepository.findAllCustom();
    }

    @Override
    public Map<String, Object> getShipLimits() {
        Map<String, Object> limits = new HashMap<>();

        limits.put("prodYear", getPropLimits(Ship.ProdDate.MIN, Ship.ProdDate.MAX));

        limits.put("crewSize", getPropLimits(Ship.CrewSize.MIN, Ship.CrewSize.MAX));

        limits.put("speed", getPropLimits(Ship.Speed.MIN, Ship.Speed.MAX));

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
    public void saveShip(Ship ship) {
        shipRepository.save(ship);
    }

    private Map<String, Number> getPropLimits(Number min, Number max) {
        Map<String, Number> propLimits = new HashMap<>();

        propLimits.put("min", min);
        propLimits.put("max", max);

        return propLimits;
    }


}
