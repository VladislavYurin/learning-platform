package ru.mentor.testUtil;

import com.google.protobuf.Timestamp;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.constant.Role;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.kafka.StudentReminderNotificationPayload;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class TestDataGenerator {
    public static final String TEST_LINK = "link";
    public static final String TEST_DESCRIPTION = "text";
    public static final int TEST_MAX_PARTICIPANTS = 10;
    public static final String TEST_PASSWORD = "password";
    public static final String TEST_UUID = UUID.randomUUID().toString();

    public static final String TEST_MENTOR_NAME = "mentor";
    public static final String TEST_MENTOR_FIRST_NAME = "John";
    public static final String TEST_MENTOR_LAST_NAME = "Doe";
    public static final String TEST_MENTOR_TG_NICKNAME = "@johndoe";

    public static final String TEST_USER_NAME = "participant";
    public static final String TEST_USER_FIRST_NAME = "Alice";
    public static final String TEST_USER_LAST_NAME = "Jane";
    public static final String TEST_USER_TG_NICKNAME = "@janet";
    public static final LocalDateTime DEFAULT_START_TIME = LocalDateTime.of(2025, 1, 15, 13, 0);

    public static UserEntity getTestMentorUser() {
        return UserEntity.builder()
                .id(1L)
                .username(TEST_MENTOR_NAME)
                .password(TEST_PASSWORD)
                .role(Role.MENTOR)
                .firstName(TEST_MENTOR_FIRST_NAME)
                .lastName(TEST_MENTOR_LAST_NAME)
                .tgNickname(TEST_MENTOR_TG_NICKNAME)
                .build();
    }

    public static UserEntity getTestParticipantUser() {
        return UserEntity.builder()
                .id(2L)
                .username(TEST_USER_NAME)
                .password(TEST_PASSWORD)
                .role(Role.USER)
                .firstName(TEST_USER_FIRST_NAME)
                .lastName(TEST_USER_LAST_NAME)
                .tgNickname(TEST_USER_TG_NICKNAME)
                .build();
    }

    public static UserEntity getAnotherTestParticipantUser() {
        return UserEntity.builder()
                .id(3L)
                .username("participant2")
                .password("pass1234")
                .role(Role.USER)
                .firstName("Ivan")
                .lastName("Petrov")
                .tgNickname("@ivanpetrov")
                .build();
    }

    public static MentorTimeSlotEntity createTestSlot(Long id, Set<UserEntity> participants, boolean isActive) {
        return MentorTimeSlotEntity.builder()
                .id(id)
                .mentor(getTestMentorUser())
                .startTime(DEFAULT_START_TIME)
                .endTime(LocalDateTime.of(2025, 1, 15, 14, 0))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(TEST_MAX_PARTICIPANTS)
                .meetingLink(TEST_LINK)
                .description(TEST_DESCRIPTION)
                .createdAt(LocalDateTime.of(2025, 1, 14, 10, 0))
                .isActive(isActive)
                .meetingParticipants(participants)
                .build();
    }

    public static MentorTimeSlotEntity createTestSlot(Long id, Set<UserEntity> participants, boolean isActive,
                                                      LocalDateTime start, LocalDateTime end) {
        return MentorTimeSlotEntity.builder()
                .id(id)
                .mentor(getTestMentorUser())
                .startTime(start)
                .endTime(end)
                .slotType(CalendarSlotType.GROUP)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(TEST_MAX_PARTICIPANTS)
                .meetingLink(TEST_LINK)
                .description(TEST_DESCRIPTION)
                .createdAt(LocalDateTime.of(2025, 1, 14, 10, 0))
                .isActive(isActive)
                .meetingParticipants(participants)
                .build();
    }

    public static TimeSlotResponse createTestTimeSlotGrpcResponse(Timestamp start, Timestamp end,
                                                                  Timestamp createdAt) {
        return TimeSlotResponse.newBuilder()
                .setRqUid(TestDataGenerator.TEST_UUID)
                .setSlotId(1L)
                .setMentorId(1L)
                .setStartTime(start)
                .setEndTime(end)
                .setSlotType(SlotType.INDIVIDUAL)
                .setSlotMeetingType(SlotMeetingType.COMMUNICATION)
                .setMaxParticipants(TestDataGenerator.TEST_MAX_PARTICIPANTS)
                .setMeetingLink(TestDataGenerator.TEST_LINK)
                .setDescription(TestDataGenerator.TEST_DESCRIPTION)
                .setCreatedAt(createdAt)
                .build();
    }

    public static MentorTimeSlotCreateRequest createTestMentorTimeSlotCreateRequest(LocalDateTime start,
                                                                                    LocalDateTime end) {

        return MentorTimeSlotCreateRequest.builder()
                .startTime(start)
                .endTime(end)
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(TestDataGenerator.TEST_MAX_PARTICIPANTS)
                .meetingLink(TestDataGenerator.TEST_LINK)
                .description(TestDataGenerator.TEST_DESCRIPTION)
                .build();
    }

    public static CreateTimeSlotRequest createTestCreateTimeSlotRequest(Timestamp start,
                                                                        Timestamp end) {

        return CreateTimeSlotRequest.newBuilder()
                .setStartTime(start)
                .setEndTime(end)
                .setSlotType(SlotType.INDIVIDUAL)
                .setSlotMeetingType(SlotMeetingType.COMMUNICATION)
                .setMaxParticipants(TestDataGenerator.TEST_MAX_PARTICIPANTS)
                .setMeetingLink(TestDataGenerator.TEST_LINK)
                .setDescription(TestDataGenerator.TEST_DESCRIPTION)
                .build();
    }

    public static StudentReminderNotificationPayload createTestStudentReminderNotificationPayload(
            MentorTimeSlotEntity testSlot,
            UserEntity testUser) {

        return StudentReminderNotificationPayload.builder()
                .studentName(testUser.getFirstName())
                .calendarSlotTime(testSlot.getStartTime())
                .mentorName(testSlot.getMentor().getFirstName())
                .slotMeetingType(testSlot.getSlotMeetingType().toString())
                .slotType(testSlot.getSlotType().toString())
                .description(testSlot.getDescription())
                .meetingLink(testSlot.getMeetingLink())
                .build();
    }
}
