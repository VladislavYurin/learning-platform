package ru.mentor.testUtil;

import com.google.protobuf.Timestamp;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;

public class TestConstantHolder {

    public static String requestId = "test-rq-id";
    public static String nodeId = "TestNodeId";
    public static String apiKey = "TestApiKey";
    public static String notFoundExceptionText = "not found";
    public static String grpcExceptionText = "Ошибка gRPC";
    public static int zero = 0;
    public static int pageNumber = 0;
    public static String pageNumberRequestParameter = "pageNumber";
    public static int pageSize = 1;
    public static final String pageSizeRequestParameter = "pageSize";
    public static boolean isActiveTrue = true;
    public static boolean isActiveFalse = false;
    public static int totalElementsCount = 1;
    public static int totalPagesCount = 1;

    public static long courseId = 1L;
    public static long authorId = 100L;
    public static String courseTitle = "Course title";
    public static String courseDescription = "Course description";

    public static long courseTagId = 1L;
    public static String courseTagName = "Test courseTagName";

    public static long moduleId = 1L;
    public static String moduleTitle = "Module title";
    public static String moduleContent = "Content";
    public static int moduleOrderNumber = 5;

    public static String mentorIdRequestParameter = "mentorId";
    public static long mentorId = 1L;
    public static String mentorName = "mentor";
    public static String mentorFirstName = "John";
    public static String mentorLastName = "Doe";
    public static String mentorTgNickname = "@johndoe";

    public static String testPassword = "password";

    public static long userId = 2L;
    public static String username = "participant";
    public static String userFirstName = "Alice";
    public static String userLastName = "Jane";
    public static String userTgNickname = "@janet";

    public static long anotherUserId = 3L;
    public static String anotherUserName = "participant2";
    public static String anotherUserPassword = "pass1234";
    public static String anotherUserFirstName = "Ivan";
    public static String anotherUserLastName = "Petrov";
    public static String anotherUserTgNickname = "@ivanpetrov";

    public static long tgChatId = 123L;

    public static long timeSlotId = 1L;

    public static SlotType grpcSlotType = SlotType.INDIVIDUAL;
    public static SlotMeetingType grpcSlotMeetingType = SlotMeetingType.ACCEPTING;
    public static CalendarSlotType slotType = CalendarSlotType.INDIVIDUAL;
    public static CalendarSlotMeetingType slotMeetingType = CalendarSlotMeetingType.ACCEPTING;
    public static int maxParticipants = 10;
    public static String meetingLink = "Test meeting link";
    public static String slotDescription = "Test slot description";
    public static DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static LocalDateTime startTime = LocalDateTime.of(2025, 1, 15, 13, 0);
    public static LocalDateTime endTime = LocalDateTime.of(2025, 1, 15, 14, 0);
    public static LocalDateTime createdAt = LocalDateTime.of(2025, 1, 14, 10, 0);

    public static Timestamp startTimestamp = Timestamp.newBuilder().setSeconds(1736946000L).build();   // 2025-01-15 13:00
    public static Timestamp endTimestamp = Timestamp.newBuilder().setSeconds(1736949600L).build();     // 2025-01-15 14:00
    public static Timestamp createdAtTimestamp = Timestamp.newBuilder().setSeconds(1736848800L).build(); // 2025-01-14 10:00

    public static String avatarBucket = "avatars";
    public static long avatarMaxSizeBytes = 5_242_880L;
    public static List<String> avatarAllowedExtensions = List.of("jpg", "jpeg", "png");
    public static List<String> avatarAllowedContentTypes = List.of("image/jpeg", "image/png");
    public static byte[] avatarMinimalContent = new byte[] { 1 };
    public static String avatarContentTypeJpeg = "image/jpeg";
    public static String avatarContentTypeText = "text/plain";
    public static String avatarFilenameJpg = "avatar.jpg";
    public static String avatarFilenameGif = "avatar.gif";
    public static String avatarFilenameNoDot = "avatar";
}
