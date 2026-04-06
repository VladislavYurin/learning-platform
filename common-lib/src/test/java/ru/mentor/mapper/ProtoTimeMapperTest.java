package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProtoTimeMapperTest {

    private final ProtoTimeMapper mapper = new ProtoTimeMapperImpl();

    @Test
    void toLocalDateTime_null_returnsNull() {
        Assertions.assertNull(mapper.toLocalDateTime(null));
    }

    @Test
    void toTimestamp_null_returnsNull() {
        Assertions.assertNull(mapper.toTimestamp(null));
    }

    @Test
    void toLocalDateTime_and_toTimestamp_roundTrip_preservesUtcInstant() {
        LocalDateTime original = LocalDateTime.of(2026, 4, 2, 18, 45, 30, 123_456_789);
        Timestamp proto = mapper.toTimestamp(original);
        LocalDateTime back = mapper.toLocalDateTime(proto);
        Assertions.assertEquals(original, back);
    }

    @Test
    void toLocalDateTime_epochBoundary_convertsToUtc() {
        Timestamp ts = Timestamp.newBuilder().setSeconds(1_700_000_000L).setNanos(500_000_000).build();
        LocalDateTime ldt = mapper.toLocalDateTime(ts);
        Timestamp again = mapper.toTimestamp(ldt);
        Assertions.assertEquals(ts.getSeconds(), again.getSeconds());
        Assertions.assertEquals(ts.getNanos(), again.getNanos());
    }
}
