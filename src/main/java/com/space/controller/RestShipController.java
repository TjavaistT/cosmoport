package com.space.controller;

import com.space.exceptions.BadRequest;
import com.space.exceptions.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.space.controller.ShipSpecification.*;

@RestController
@RequestMapping("/rest/ships")
public class RestShipController {

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "3";

    private final ShipService shipService;

    public RestShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping
    public List<Ship> listByFilter(
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

            @RequestParam(value = "pageNumber", required = false, defaultValue = DEFAULT_PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize,

            @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order
    ){
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, order.getFieldName());

        return shipService.selectWithFilter(
                     shipService.filterByName(name)
                .and(shipService.filterByPlanet(planet))
                .and(shipService.filterByShipType(shipType))
                .and(shipService.filterByProdDate(after, before))
                .and(shipService.filterByUsed(isUsed))
                .and(shipService.filterBySpeed(minSpeed, maxSpeed))
                .and(shipService.filterByCrewSize(minCrewSize, maxCrewSize))
                .and(shipService.filterByRating(minRating, maxRating)),
                pageable);
    }

    @GetMapping("/count")
    public long count(
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
            @RequestParam(value = "maxRating", required = false) Double maxRating
    ){
        return shipService.getCount(
                     shipService.filterByName(name)
                .and(shipService.filterByPlanet(planet))
                .and(shipService.filterByShipType(shipType))
                .and(shipService.filterByProdDate(after, before))
                .and(shipService.filterByUsed(isUsed))
                .and(shipService.filterBySpeed(minSpeed, maxSpeed))
                .and(shipService.filterByCrewSize(minCrewSize, maxCrewSize))
                .and(shipService.filterByRating(minRating, maxRating))
        );
    }


    @PostMapping
    public ResponseEntity<Ship> createShip(
            @Validated(Ship.Create.class) @RequestBody Ship ship,
            BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()) {
            throw new BadRequest();
        } else {
            shipService.save(ship);
            return new ResponseEntity<>(ship, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public Ship getShipById(@PathVariable long id){
        try {

            if(id == 0) { throw new BadRequest(); }

            Ship searchedShip = shipService.getShipById(id);

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

            if(isFieldChange){ shipService.save(savedShip); }

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
    public void deleteShipById(@PathVariable long id){
        try {
            if(id == 0) { throw new BadRequest(); }

            if(!shipService.isExistShipById(id)){ throw new NotFoundException(); }

            shipService.deleteShipById(id);

        } catch (NumberFormatException ex){
            throw new BadRequest();
        }
    }

}
