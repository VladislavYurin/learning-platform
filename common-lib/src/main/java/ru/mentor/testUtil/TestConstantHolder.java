package ru.mentor.testUtil;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;

public class TestConstantHolder {

    public static String requestId = UUID.randomUUID().toString();
    public static String nodeId = "TestNodeId";
    public static String apiKey = "TestApiKey";
    public static LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC).withNano(0);
    public static String notFoundExceptionText = "not found";
    public static String grpcExceptionText = "Ошибка gRPC";
    public static int pageNumber = 0;
    public static int pageSize = 1;
    public static boolean isActive = true;
    public static int totalElementsCount = 1;
    public static int totalPagesCount = 1;
    public static long mentorId = 1L;

    public static long courseId = 1L;
    public static long authorId = 100L;
    public static String courseTitle = "Course title";
    public static String courseDescription = "Course description";

    public static Long tagId = 1L;
    public static long courseTagId = 1L;
    public static String courseTagName = "Test courseTagName";
    public static Timestamp courseTagCreatedAtTimestamp = Timestamp.newBuilder()
                                                              .setSeconds(createdAt.toEpochSecond(ZoneOffset.UTC))
                                                              .build();
    public static long moduleId = 1L;
    public static String moduleTitle = "Module title";
    public static String moduleContent = "Content";
    public static int moduleOrderNumber = 5;

    public static long userId = 1L;
    public static String username = "testuser";
    public static String firstName = "Test";
    public static String lastName = "User";
    public static String tgNickname = "@testuser";
    public static long tgChatId = 123L;

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

    public static Long timeSlotId = 1L;
    public static LocalDateTime slotStartTime = LocalDateTime.now(ZoneOffset.UTC).withNano(0);
    public static LocalDateTime slotEndTime = slotStartTime.plusHours(1);

    public static SlotType grpcSlotType = SlotType.INDIVIDUAL;
    public static SlotMeetingType grpcSlotMeetingType = SlotMeetingType.ACCEPTING;
    public static CalendarSlotType slotType = CalendarSlotType.INDIVIDUAL;
    public static CalendarSlotMeetingType slotMeetingType = CalendarSlotMeetingType.ACCEPTING;
    public static int maxParticipants = 10;
    public static String meetingLink = "Test meeting link";
    public static String slotDescription = "Test slot description";

    public static Timestamp slotStartTimestamp = Timestamp.newBuilder()
                                                          .setSeconds(slotStartTime.toEpochSecond(
                                                                  ZoneOffset.UTC))
                                                          .build();
    public static Timestamp slotEndTimestamp = Timestamp.newBuilder()
                                                        .setSeconds(slotEndTime.toEpochSecond(
                                                                ZoneOffset.UTC))
                                                        .build();
    public static Timestamp slotCreatedAtTimestamp = Timestamp.newBuilder()
                                                              .setSeconds(createdAt.toEpochSecond(
                                                                      ZoneOffset.UTC))
                                                              .build();

}
