package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

public interface ShipService {

    List<Ship> selectWithFilter(Specification<Ship> specification, Pageable pageable);

    Map<String, Object> getShipLimits();

    Ship getShipById(Long id);

    void deleteShipById(long id);

    void save(Ship searchedShip);

    long getCount(Specification<Ship> specification);

    List<Ship> findAll();

    boolean isExistShipById(long id);

    Specification<Ship> filterByName(String name);

    Specification<Ship> filterByPlanet(String planet);

    Specification<Ship> filterByShipType(ShipType shipType);

    Specification<Ship> filterByProdDate(Long after, Long before);

    Specification<Ship> filterByUsed(Boolean isUsed);

    Specification<Ship> filterBySpeed(Double minSpeed, Double maxSpeed);

    Specification<Ship> filterByCrewSize(Integer minCrewSize, Integer maxCrewSize);

    Specification<Ship> filterByRating(Double minRating, Double maxRating);
}
