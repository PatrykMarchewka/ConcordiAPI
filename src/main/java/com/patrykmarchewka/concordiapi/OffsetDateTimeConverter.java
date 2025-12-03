package com.patrykmarchewka.concordiapi;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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
        return offsetDateTime == null ? null : offsetDateTime.truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public OffsetDateTime convertToDatabaseColumn(final OffsetDateTime offsetDateTime) {
        return converted(offsetDateTime);
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(final OffsetDateTime offsetDateTime) {
        return converted(offsetDateTime);
    }

    public static String formatDate(final OffsetDateTime offsetDateTime){
        return offsetDateTime != null ? offsetDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxxx")) : null;
    }

    public static OffsetDateTime parseDate(final String dateString){
        return dateString != null ? OffsetDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxxx")) : null;
    }
}
