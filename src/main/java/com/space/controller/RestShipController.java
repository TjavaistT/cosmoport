package com.space.controller;

import com.space.exceptions.BadRequest;
import com.space.exceptions.NotFoundException;
import com.space.model.ship.Ship;
import com.space.model.ship.ShipType;
import com.space.service.ShipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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

    @PostMapping
    public ResponseEntity<Ship> createShip(
            @Validated(Ship.Create.class) @RequestBody Ship ship,
            BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()) {
            throw new BadRequest();
        } else {
            shipService.saveShip(ship);
            return new ResponseEntity<>(ship, HttpStatus.OK);
        }
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
    public ResponseEntity<Ship> updateShipById(
            @PathVariable Long id,
            @Validated(Ship.Update.class) @RequestBody Ship requestShip
    ){
        try {

            if (id < 1) {
                throw new BadRequest();
            }

            Ship savedShip = shipService.getShipById(id);

            if (savedShip == null) {
                throw new NotFoundException();
            }

            boolean isFieldChange = false ;

            if(requestShip.getName() != null) {
                savedShip.setName(requestShip.getName());
                isFieldChange = true;
            }

            if(requestShip.getPlanet() != null){
                savedShip.setPlanet(requestShip.getPlanet());
                isFieldChange = true;
            }
            //TODO - доработать
            //ShipType.valueOf(shipType)

            if(requestShip.getShipType() != null) {
                savedShip.setShipType(requestShip.getShipType() );
                isFieldChange = true;
            }

            if(requestShip.getProdDate() != null) {
                savedShip.setProdDate(requestShip.getProdDate());
                isFieldChange = true;
            }

            if(requestShip.isUsed() != null){
                savedShip.setIsUsed(requestShip.isUsed());
                isFieldChange = true;
            }

            if(requestShip.getSpeed() != null) {
                savedShip.setSpeed(requestShip.getSpeed());
                isFieldChange = true;
            }

            if(requestShip.getCrewSize() != null) {
                savedShip.setCrewSize(requestShip.getCrewSize());
                isFieldChange = true;
            }

            if(isFieldChange){ shipService.saveShip(savedShip); }

            return new ResponseEntity<>(savedShip, HttpStatus.OK);

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

    private String toValidString(@RequestParam(value = "name", required = false) String name) {
        name = name != null ? name.trim() : "";
        return name;
    }

}
