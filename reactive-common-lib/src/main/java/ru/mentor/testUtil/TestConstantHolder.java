package ru.mentor.testUtil;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;

public final class TestConstantHolder {

    public static final String NOT_FOUND_EXCEPTION_TEXT = "Slots not found";
    public static final String EMPTY_REQUEST_TEXT = "Empty request";

    public static final String REQUEST_ID = UUID.randomUUID().toString();
    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 1, 1, 12, 0);
    public static final int PAGE_NUMBER = 0;
    public static final int PAGE_SIZE = 1;
    public static final long TOTAL_ELEMENTS_COUNT = 1L;
    public static final int TOTAL_PAGES_COUNT = 1;
    public static final long MENTOR_ID = 10L;
    public static final long ADMIN_ID = 15L;

    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;
    public static final long CREATED_AT_EPOCH_SECONDS = CREATED_AT.toEpochSecond(ZONE_OFFSET);

    public static final long SLOT_ID = 1L;
    public static final LocalDateTime SLOT_START_TIME = LocalDateTime.of(2025, 1, 10, 10, 0);
    public static final LocalDateTime SLOT_END_TIME = SLOT_START_TIME.plusHours(1);
    public static final long SLOT_START_EPOCH_SECONDS = SLOT_START_TIME.toEpochSecond(ZONE_OFFSET);
    public static final long SLOT_END_EPOCH_SECONDS = SLOT_END_TIME.toEpochSecond(ZONE_OFFSET);

    public static final CalendarSlotType SLOT_TYPE = CalendarSlotType.INDIVIDUAL;
    public static final CalendarSlotMeetingType SLOT_MEETING_TYPE = CalendarSlotMeetingType.ACCEPTING;
    public static final SlotType GRPC_SLOT_TYPE = SlotType.INDIVIDUAL;
    public static final SlotMeetingType GRPC_SLOT_MEETING_TYPE = SlotMeetingType.ACCEPTING;

    public static final int MAX_PARTICIPANTS = 10;
    public static final String MEETING_LINK = "https://meet.test";
    public static final String SLOT_DESCRIPTION = "Test slot description";

    public static final long USER_ID = 100L;
    public static final String USERNAME = "test-user";
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Doe";
    public static final String TG_NICKNAME = "@john";
    public static final long TG_CHAT_ID = 555L;

    public static final Timestamp SLOT_START_TIMESTAMP =
            Timestamp.newBuilder()
                     .setSeconds(
                             SLOT_START_EPOCH_SECONDS)
                     .build();
    public static final Timestamp SLOT_END_TIMESTAMP =
            Timestamp.newBuilder()
                     .setSeconds(SLOT_END_EPOCH_SECONDS)
                     .build();
    public static final Timestamp SLOT_CREATED_AT_TIMESTAMP =
            Timestamp.newBuilder()
                     .setSeconds(
                             CREATED_AT_EPOCH_SECONDS)
                     .build();

    public static final long COURSE_ID = 1L;
    public static final String COURSE_TITLE = "test-course-title";
    public static final String COURSE_DESCRIPTION = "test-course-description";
    public static final Boolean IS_ACTIVE_COURSE = true;
    public static final Long COURSE_AUTHOR_ID = MENTOR_ID;

    public static final long MODULE_ID = 2L;
    public static final String MODULE_TITLE = "test-module-title";
    public static final int MODULE_ORDER_NUMBER = 5;
    public static final String MODULE_CONTENT = "test-module-content";
    public static final Boolean IS_ACTIVE_MODULE = true;
    public static final long MODULE_CREATED_AT_EPOCH_SECONDS = CREATED_AT_EPOCH_SECONDS;
    public static final Timestamp MODULE_CREATED_AT_TIMESTAMP =
            Timestamp.newBuilder()
                     .setSeconds(
                             MODULE_CREATED_AT_EPOCH_SECONDS)
                     .build();

}
