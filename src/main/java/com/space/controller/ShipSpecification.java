package com.space.controller;

import com.space.model.ship.Ship;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class ShipSpecification {

    public static Specification<Ship> paramContains(String param, Object value) {
        return (Specification<Ship>) (root, query, criteria) -> criteria.like(root.get(param), "%" + value + "%");
    }

    public static Specification<Ship> paramEquals(String param, Object value) {
        return (Specification<Ship>) (root, query, criteria) -> criteria.equal(root.get(param), value);
    }

    public static Specification<Ship> paramGreaterEqual(String param, Number value) {
        return (Specification<Ship>) (root, query, criteria) -> criteria.ge(root.get(param), value);
    }

    public static Specification<Ship> paramLessEqual(String param, Number value) {
        return (Specification<Ship>) (root, query, criteriaBuilder) -> criteriaBuilder.le(root.get(param), value);
    }

    public static Specification<Ship> dateGreaterEqual(String param, Date value) {
        return (Specification<Ship>) (root, query, criteria) -> criteria.greaterThanOrEqualTo(root.get(param), value);
    }

    public static Specification<Ship> dateLessEqual(String param, Date value) {
        return (Specification<Ship>) (root, query, criteria) -> criteria.lessThan(root.get(param), value);
    }
}