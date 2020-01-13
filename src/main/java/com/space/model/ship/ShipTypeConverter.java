package com.space.model.ship;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class ShipTypeConverter implements AttributeConverter<ShipType, String> {

    @Override
    public String convertToDatabaseColumn(ShipType attribute) {
        return attribute.name();
    }

    @Override
    public ShipType convertToEntityAttribute(String dbData) {
        return Stream.of(ShipType.values())
                .filter(type -> type.name().equals(dbData))
                .findFirst()
                .orElse(null);
    }
}
