package com.space.repository;

import com.space.model.ship.Ship;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipRepository extends JpaRepository<Ship, Long>, JpaSpecificationExecutor<Ship> {


    @Query(value = "SELECT s FROM #{#entityName} s "
            +
            "WHERE "
            + "s.name LIKE CONCAT('%', :name, '%') " +
            " AND "
            + "s.planet LIKE CONCAT('%',:planet,'%') " +
            " AND "
            + "s.shipType IN (:shipTypes)" +
            " AND ("
            + " s.isUsed = :isUsed "
            + " OR "
            + " s.isUsed <> :isNew "
            + ")" +
            " AND "
            + " s.speed >= :minSpeed " +
            " AND "
            + " s.speed <= :maxSpeed" +
            " AND "
            + " s.crewSize >= :minCrewSize " +
            " AND "
            + " s.crewSize <= :maxCrewSize" +
            " AND "
            + " s.rating >= :minRating" +
            " AND "
            + " s.rating <= :maxRating "

    )
    List<Ship> selectShips(
            @Param("name") String name,
            @Param("planet") String planet,
            @Param("shipTypes") List<String> shipTypes,
            @Param("isUsed") Boolean isUsed,
            @Param("isNew") Boolean isNew,
            @Param("minSpeed") Double minSpeed,
            @Param("maxSpeed") Double maxSpeed,
            @Param("minCrewSize") Integer minCrewSize,
            @Param("maxCrewSize") Integer maxCrewSize,
            @Param("minRating") Double minRating,
            @Param("maxRating") Double maxRating,

            Sort sort
            );

    List<Ship> findAllCustom();
}