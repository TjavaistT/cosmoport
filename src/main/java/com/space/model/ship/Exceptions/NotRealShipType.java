package com.space.model.ship.Exceptions;

public class NotRealShipType extends RuntimeException {
    public NotRealShipType() {
        super("Такого типа корабля не существует");
    }
}
