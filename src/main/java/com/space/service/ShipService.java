package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.ship.Ship;
import com.space.model.ship.ShipType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ShipService {

    Collection<Ship> selectShips(
            String name, String planet,
            ShipType shipType,
            Long after, Long before,
            Boolean isUsed, Boolean isNew,
            Double minSpeed, Double maxSpeed,
            Integer minCrewSize, Integer maxCrewSize,
            Double minRating, Double maxRating,

            Integer pageNumber, Integer pageSize,

            ShipOrder order);

    List<Ship> findAll();

    Map<String, Object> getShipLimits();

    Ship getShipById(Long id);

    void deleteShipById(long id);

    void saveShip(Ship searchedShip);
}
