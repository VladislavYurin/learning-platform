package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Маппер для преобразования времени между {@link com.google.protobuf.Timestamp}
 * и {@link java.time.LocalDateTime}.
 * <p>Все преобразования выполняются в часовом поясе {@link java.time.ZoneOffset#UTC}.
 * Возвращает {@code null}, если входное значение равно {@code null}.</p>
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProtoTimeMapper {

    /**
     * Преобразует {@link Timestamp} в {@link LocalDateTime} (UTC).
     */
    default LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) return null;
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * Преобразует {@link LocalDateTime} (UTC) в {@link Timestamp}.
     */
    default Timestamp toTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
