package com.space.service;

import com.space.model.ship.Ship;
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
}
