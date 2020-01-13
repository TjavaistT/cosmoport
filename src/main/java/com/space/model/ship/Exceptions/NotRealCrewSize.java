package com.space.model.ship.Exceptions;

public class NotRealCrewSize extends RuntimeException {
    public NotRealCrewSize() {
        super("Размер экипажа не попадает в допустимый диапазон значений 1..9999 включительно.");
    }
}
