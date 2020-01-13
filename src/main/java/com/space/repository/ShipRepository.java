package com.space.repository;

import com.space.model.ship.Ship;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface ShipRepository extends JpaRepository<Ship, Long>, JpaSpecificationExecutor<Ship> {
}