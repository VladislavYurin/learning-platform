package ru.mentor.mapper;

import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface DateTimeMapper {

    default ZonedDateTime asZonedDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault());
    }

    default LocalDateTime asLocalDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null ? null : zonedDateTime.toLocalDateTime();
    }
}
