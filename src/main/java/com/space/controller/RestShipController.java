package com.space.controller;

import com.space.exceptions.BadRequest;
import com.space.exceptions.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/rest/ships")
public class RestShipController {

    private static final int DEFAULT_PAGE_NUMBER = 0;

    private static final int DEFAULT_PAGE_SIZE = 3;

    private final ShipService shipService;

    public RestShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping
    public Collection<Ship> listByFilterWithSortingAndPagination(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,

            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,

            @RequestParam(value = "order", required = false) ShipOrder order
    ){

        name = toValidString(name);

        planet = toValidString(planet);

        boolean isNew;
        if(isUsed == null){
            isUsed = true;
            isNew = true;
        } else {
            isNew = !isUsed;
        }

        pageNumber = pageNumber == null ? DEFAULT_PAGE_NUMBER : pageNumber;
        pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;

        return shipService.selectShips(
                name, planet,
                shipType,
                after, before,
                isUsed, isNew,
                minSpeed, maxSpeed,
                minCrewSize, maxCrewSize,
                minRating, maxRating,

                pageNumber, pageSize,

                order
        );
    }

    @PostMapping
    public Map<String, String> create(
            @RequestBody Map<String, String> requestShip
    ){

        String name = requestShip.get("name");
        String planet = requestShip.get("planet");
        String shipType = requestShip.get("shipType");
        String prodDate = requestShip.get("prodDate");
        String speed = requestShip.get("speed");
        String crewSize = requestShip.get("crewSize");

        if(isValidParams(name, planet, shipType, prodDate, speed, crewSize)){ throw new BadRequest(); }

        try {
            boolean isUsed = false;

            if(requestShip.get("isUsed") != null){
                isUsed = Boolean.parseBoolean(requestShip.get("isUsed"));
            }

            long max = shipService.findAll().stream()
                    .map(ship -> ship.getId())
                    .max(Long::compareTo)
                    .orElse(0L);
            long id = max + 1;

            Calendar productionDate = Calendar.getInstance();
            productionDate.setTimeInMillis(Long.parseLong(prodDate));

            Ship currentShip = new Ship(
                    id,
                    name,
                    planet,
                    shipType,
                    productionDate.getTime(),
                    isUsed,
                    Double.parseDouble(speed),
                    Integer.parseInt(crewSize)
            );

            shipService.saveShip(currentShip);

            return currentShip.toJsonMap();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequest();
        }
    }

    @GetMapping("/count")
    public int count(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,

            @RequestParam(value = "order", required = false) ShipOrder order
    ){
        name = toValidString(name);

        planet = toValidString(planet);

        boolean isNew;
        if(isUsed == null){
            isUsed = true;
            isNew = true;
        } else {
            isNew = !isUsed;
        }

        Integer pageNumber = 0;

        Integer pageSize = 999999;

        Collection<Ship> ships = shipService.selectShips(
                name, planet,
                shipType,
                after, before,
                isUsed, isNew,
                minSpeed, maxSpeed,
                minCrewSize, maxCrewSize,
                minRating, maxRating,

                pageNumber, pageSize,

                order
        );

        return ships.size();
    }

    @GetMapping("/{id}")
    public Ship getShipById(@PathVariable String id){
        try {

            long ID = Long.parseLong(id);

            if(ID == 0) { throw new BadRequest(); }

            Ship searchedShip = shipService.getShipById(ID);

            if(searchedShip == null){ throw new NotFoundException(); }

            return searchedShip;

        } catch (NumberFormatException ex){
            throw new BadRequest();
        }
    }

    @PostMapping("/{id}")
    public Map<String, String> updateShipById(
            @PathVariable String id,
            @RequestBody Map<String, String> requestShip
    ){
        try {

            long ID = Long.parseLong(id);

            if (ID == 0) {
                throw new BadRequest();
            }

            Ship savedShip = shipService.getShipById(ID);

            if (savedShip == null) {
                throw new NotFoundException();
            }

            if(requestShip.get("name") != null && requestShip.get("name").isEmpty()){
                throw new BadRequest();
            } else if (requestShip.get("name") != null) {
                savedShip.setName(requestShip.get("name"));
            }

            if(requestShip.get("planet") != null){
                savedShip.setPlanet(requestShip.get("planet"));
            }

            if(requestShip.get("shipType") != null) {
                savedShip.setShipType(requestShip.get("shipType"));
            }

            if(requestShip.get("prodDate") != null) {
                long prodDate = Long.parseLong(requestShip.get("prodDate"));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(prodDate);
                savedShip.setProdDate(calendar.getTime());
            }

            if(requestShip.get("isUsed") != null) {
                Boolean isUsed = Boolean.parseBoolean(requestShip.get("isUsed"));
                savedShip.setIsUsed(isUsed);
            }

            if(requestShip.get("speed") != null) {
                double speed = Double.parseDouble(requestShip.get("speed"));
                savedShip.setSpeed(speed);
            }

            if(requestShip.get("crewSize") != null) {
                int crewSize = Integer.parseInt(requestShip.get("crewSize"));
                savedShip.setCrewSize(crewSize);
            }

            shipService.saveShip(savedShip);

            return savedShip.toJsonMap();

        } catch (NotFoundException nfe){
            nfe.printStackTrace();
            throw new NotFoundException();
        } catch (RuntimeException ex){
            ex.printStackTrace();
            throw new BadRequest();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteShipById(@PathVariable String id){
        try {
            long ID = Long.parseLong(id);

            if(ID == 0) { throw new BadRequest(); }

            Ship searchedShip = shipService.getShipById(ID);

            if(searchedShip == null){ throw new NotFoundException(); }

            shipService.deleteShipById(ID);

        } catch (NumberFormatException ex){
            throw new BadRequest();
        }
    }

    private boolean isValidParams(String name, String planet, String shipType, String prodDate, String speed, String crewSize) {
        return name == null || name.isEmpty() || name.length() > 50 ||
                planet == null || planet.isEmpty() || planet.length() > 50 ||
                shipType == null ||
                prodDate == null ||
                speed == null ||
                crewSize == null;
    }

    private String toValidString(@RequestParam(value = "name", required = false) String name) {
        name = name != null ? name.trim() : "";
        return name;
    }

}
