package ru.mentor.testUtil;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.constant.Role;
import ru.mentor.entity.UserEntity;

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


    public static TimeSlotResponse createTestTimeSlotGrpcResponse(
            Timestamp start, Timestamp end,
            Timestamp createdAt) {
        return TimeSlotResponse.newBuilder()
                               .setRequestId(TestDataGenerator.TEST_UUID)
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



    public static CreateTimeSlotRequest createTestCreateTimeSlotRequest(
            Timestamp start,
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



}
