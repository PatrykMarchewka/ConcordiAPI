package com.patrykmarchewka.concordiapi;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.OffsetDateTime;

@Converter(autoApply = true)
public final class OffsetDateTimeConverter implements AttributeConverter<OffsetDateTime, OffsetDateTime> {

    public static OffsetDateTime nowConverted(){
        return converted(OffsetDateTime.now());
    }

    public static OffsetDateTime MAXConverted(){
        return converted(OffsetDateTime.MAX);
    }

    public static OffsetDateTime MINConverted(){
        return converted(OffsetDateTime.MIN);
    }

    public static OffsetDateTime converted(final OffsetDateTime offsetDateTime){
        return offsetDateTime == null ? null : offsetDateTime.withNano((offsetDateTime.getNano() / 1000) * 1000);
    }

    @Override
    public OffsetDateTime convertToDatabaseColumn(final OffsetDateTime offsetDateTime) {
        return converted(offsetDateTime);
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(final OffsetDateTime offsetDateTime) {
        return converted(offsetDateTime);
    }
}
