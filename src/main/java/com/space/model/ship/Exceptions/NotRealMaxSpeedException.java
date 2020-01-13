package com.space.model.ship.Exceptions;

public class NotRealMaxSpeedException extends RuntimeException {
    public NotRealMaxSpeedException() {
        super("Значение максимальной скорости не попадает допустимый диапазон 0,01..0,99 включительно.");
    }
}
