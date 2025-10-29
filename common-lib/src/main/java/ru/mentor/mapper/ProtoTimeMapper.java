package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования времени между {@link com.google.protobuf.Timestamp}
 * и {@link java.time.LocalDateTime}.
 * <p>Все преобразования выполняются в часовом поясе {@link java.time.ZoneOffset#UTC}.
 * Возвращает {@code null}, если входное значение равно {@code null}.</p>
 */
@Component
@RequiredArgsConstructor
public class ProtoTimeMapper {

    /**
     * Преобразует {@link Timestamp} в {@link LocalDateTime} (UTC).
     */
    public LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) return null;
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * Преобразует {@link LocalDateTime} (UTC) в {@link Timestamp}.
     */
    public Timestamp toTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
