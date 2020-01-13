package com.space.model.ship;

abstract class ShipProps <T> {

    public T MIN;
    public T MAX;

    private T value;

    ShipProps(T number) {
        value = number;
    }

    public T getValue() {
        return value;
    }
}
