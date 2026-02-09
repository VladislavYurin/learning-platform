package ru.mentor.testUtil;

import com.google.protobuf.Timestamp;

import java.time.LocalDateTime;
import java.util.Set;

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

public class TestDataGenerator {

    public static UserEntity getAdminEntity() {
        return UserEntity.builder()
                .id(TestConstantHolder.mentorId)
                .username(TestConstantHolder.mentorName)
                .password(TestConstantHolder.testPassword)
                .role(Role.ADMIN)
                .firstName(TestConstantHolder.mentorFirstName)
                .lastName(TestConstantHolder.mentorLastName)
                .tgNickname(TestConstantHolder.mentorTgNickname)
                .build();
    }

    public static UserEntity getMentorEntity() {
        return UserEntity.builder()
                .id(TestConstantHolder.mentorId)
                .username(TestConstantHolder.mentorName)
                .password(TestConstantHolder.testPassword)
                .role(Role.MENTOR)
                .firstName(TestConstantHolder.mentorFirstName)
                .lastName(TestConstantHolder.mentorLastName)
                .tgNickname(TestConstantHolder.mentorTgNickname)
                .tgChatId(TestConstantHolder.tgChatId)
                .build();
    }

    public static UserEntity getUserEntity() {
        return UserEntity.builder()
                .id(TestConstantHolder.userId)
                .username(TestConstantHolder.username)
                .password(TestConstantHolder.testPassword)
                .role(Role.USER)
                .firstName(TestConstantHolder.userFirstName)
                .lastName(TestConstantHolder.userLastName)
                .tgNickname(TestConstantHolder.userTgNickname)
                .build();
    }

    public static UserEntity getAnotherUserEntity() {
        return UserEntity.builder()
                .id(TestConstantHolder.anotherUserId)
                .username(TestConstantHolder.anotherUserName)
                .password(TestConstantHolder.anotherUserPassword)
                .role(Role.USER)
                .firstName(TestConstantHolder.anotherUserFirstName)
                .lastName(TestConstantHolder.anotherUserLastName)
                .tgNickname(TestConstantHolder.anotherUserTgNickname)
                .build();
    }

    public static MentorTimeSlotEntity createTestSlot(Long id,
                                                      boolean isActive,
                                                      Set<UserEntity> participants) {

        return MentorTimeSlotEntity.builder()
                .id(id)
                .mentor(getMentorEntity())
                .startTime(TestConstantHolder.startTime)
                .endTime(TestConstantHolder.endTime)
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(TestConstantHolder.maxParticipants)
                .isActive(isActive)
                .meetingLink(TestConstantHolder.meetingLink)
                .description(TestConstantHolder.slotDescription)
                .createdAt(TestConstantHolder.createdAt)
                .meetingParticipants(participants)
                .build();
    }

    public static MentorTimeSlotEntity createTestSlot(Long id,
                                                      Set<UserEntity> participants,
                                                      boolean isActive,
                                                      LocalDateTime start,
                                                      LocalDateTime end) {

        return MentorTimeSlotEntity.builder()
                .id(id)
                .mentor(getMentorEntity())
                .startTime(start)
                .endTime(end)
                .slotType(CalendarSlotType.GROUP)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(TestConstantHolder.maxParticipants)
                .isActive(isActive)
                .meetingLink(TestConstantHolder.meetingLink)
                .description(TestConstantHolder.slotDescription)
                .createdAt(start.minusDays(1)
                        .minusHours(3))
                .meetingParticipants(participants)
                .build();
    }

    public static TimeSlotResponse createTestTimeSlotGrpcResponse(Timestamp start,
                                                                  Timestamp end,
                                                                  Timestamp createdAt) {

        return TimeSlotResponse.newBuilder()
                .setRequestId(TestConstantHolder.requestId)
                .setSlotId(TestConstantHolder.timeSlotId)
                .setMentorId(TestConstantHolder.mentorId)
                .setStartTime(start)
                .setEndTime(end)
                .setSlotType(SlotType.INDIVIDUAL)
                .setSlotMeetingType(SlotMeetingType.COMMUNICATION)
                .setMaxParticipants(TestConstantHolder.maxParticipants)
                .setMeetingLink(TestConstantHolder.meetingLink)
                .setDescription(TestConstantHolder.slotDescription)
                .setCreatedAt(createdAt)
                .build();
    }

    public static MentorTimeSlotCreateRequest createTestMentorTimeSlotCreateRequest(
            LocalDateTime start,
            LocalDateTime end) {

        return MentorTimeSlotCreateRequest.builder()
                .startTime(start)
                .endTime(end)
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(TestConstantHolder.maxParticipants)
                .meetingLink(TestConstantHolder.meetingLink)
                .description(TestConstantHolder.slotDescription)
                .build();
    }

    public static CreateTimeSlotRequest createTestCreateTimeSlotRequest(Timestamp start,
                                                                        Timestamp end) {

        return CreateTimeSlotRequest.newBuilder()
                .setHeader(TestGrpcStubGenerator.constructHeader())
                .setMentorId(TestConstantHolder.mentorId)
                .setStartTime(start)
                .setEndTime(end)
                .setSlotType(SlotType.INDIVIDUAL)
                .setSlotMeetingType(SlotMeetingType.COMMUNICATION)
                .setMaxParticipants(TestConstantHolder.maxParticipants)
                .setMeetingLink(TestConstantHolder.meetingLink)
                .setDescription(TestConstantHolder.slotDescription)
                .build();
    }

    public static StudentReminderNotificationPayload createTestStudentReminderNotificationPayload(
            MentorTimeSlotEntity testSlot,
            UserEntity testUser) {

        return StudentReminderNotificationPayload.builder()
                .studentName(testUser.getFirstName())
                .calendarSlotTime(testSlot.getStartTime())
                .mentorName(testSlot.getMentor().getFirstName())
                .slotMeetingType(testSlot.getSlotMeetingType()
                        .toString())
                .slotType(testSlot.getSlotType().toString())
                .description(testSlot.getDescription())
                .meetingLink(testSlot.getMeetingLink())
                .build();
    }

}