package com.space.model.ship.Exceptions;

public class NotRealProdDateException extends RuntimeException {
    public NotRealProdDateException() {
        super("Дата не попадает в диапазон значений года 2800..3019 включительно");
    }
}
