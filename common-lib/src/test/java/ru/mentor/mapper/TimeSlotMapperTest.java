package ru.mentor.mapper;

import com.google.protobuf.Timestamp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.Header;
import ru.mentor.common.MentorSlotsInfoResponse;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestDataGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@SpringBootTest(classes = {
        TimeSlotMapperImpl.class,
        UtilMapperImpl.class})
class TimeSlotMapperTest {

    @Autowired
    private TimeSlotMapper timeSlotMapper;

    @Test
    void timeSlotResponseToMentorTimeSlotDto() {
        Timestamp startTime = TestConstantHolder.startTimestamp;
        Timestamp endTime = TestConstantHolder.endTimestamp;
        Timestamp createdAt = TestConstantHolder.createdAtTimestamp;

        TimeSlotResponse grpcResponse = TestDataGenerator.createTestTimeSlotGrpcResponse(
                startTime, endTime, createdAt);

        MentorTimeSlotDto result = timeSlotMapper.timeSlotResponseToMentorTimeSlotDto(grpcResponse);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(TestConstantHolder.requestId, result.getRequestId());
        Assertions.assertEquals(TestConstantHolder.timeSlotId, result.getId());
        Assertions.assertEquals(TestConstantHolder.mentorId, result.getMentorId());
        Assertions.assertEquals(TestConstantHolder.startTime, result.getStartTime());
        Assertions.assertEquals(TestConstantHolder.endTime, result.getEndTime());
        Assertions.assertEquals(CalendarSlotType.INDIVIDUAL, result.getSlotType());
        Assertions.assertEquals(CalendarSlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        Assertions.assertEquals(
                TestConstantHolder.maxParticipants,
                result.getMaxParticipants()
        );
        Assertions.assertEquals(TestConstantHolder.meetingLink, result.getMeetingLink());
        Assertions.assertEquals(TestConstantHolder.slotDescription, result.getDescription());
        Assertions.assertEquals(TestConstantHolder.createdAt, result.getCreatedAt());
    }

    @Test
    void requestCreateToGrpcDto() {
        LocalDateTime startTime = TestConstantHolder.startTime;
        LocalDateTime endTime = TestConstantHolder.endTime;

        MentorTimeSlotCreateRequest createRequest = TestDataGenerator.createTestMentorTimeSlotCreateRequest(
                startTime, endTime);

        UserEntity testMentorUser = TestDataGenerator.getMentorEntity();

        Header header = TestGrpcStubGenerator.constructHeader();
        CreateTimeSlotRequest result = timeSlotMapper.toCreateTimeSlotRequest(
                createRequest,
                header,
                testMentorUser
        );

        Assertions.assertNotNull(result);
        Assertions.assertEquals(TestConstantHolder.requestId, result.getHeader().getRequestId());
        Assertions.assertEquals(TestConstantHolder.mentorId, result.getMentorId());
        Assertions.assertEquals(TestConstantHolder.startTimestamp.getSeconds(),
                result.getStartTime().getSeconds()
        );
        Assertions.assertEquals(TestConstantHolder.endTimestamp.getSeconds(),
                result.getEndTime().getSeconds()
        );
        Assertions.assertEquals(SlotType.INDIVIDUAL, result.getSlotType());
        Assertions.assertEquals(SlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        Assertions.assertEquals(
                TestConstantHolder.maxParticipants,
                result.getMaxParticipants()
        );
        Assertions.assertEquals(TestConstantHolder.meetingLink, result.getMeetingLink());
        Assertions.assertEquals(TestConstantHolder.slotDescription, result.getDescription());
    }

    @Test
    void grpcCreateRequestToEntity() {
        Timestamp startTime = TestConstantHolder.startTimestamp;
        Timestamp endTime = TestConstantHolder.endTimestamp;

        CreateTimeSlotRequest request = TestDataGenerator.createTestCreateTimeSlotRequest(
                startTime, endTime);
        UserEntity testMentorUser = TestDataGenerator.getMentorEntity();
        MentorTimeSlotEntity result = timeSlotMapper.toMentorTimeSlotEntity(
                request,
                testMentorUser
        );

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testMentorUser, result.getMentor());
        Assertions.assertEquals(TestConstantHolder.startTime, result.getStartTime());
        Assertions.assertEquals(TestConstantHolder.endTime, result.getEndTime());
        Assertions.assertEquals(CalendarSlotType.INDIVIDUAL, result.getSlotType());
        Assertions.assertEquals(CalendarSlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        Assertions.assertEquals(
                TestConstantHolder.maxParticipants,
                result.getMaxParticipants()
        );
        Assertions.assertEquals(TestConstantHolder.meetingLink, result.getMeetingLink());
        Assertions.assertEquals(TestConstantHolder.slotDescription, result.getDescription());
        Assertions.assertTrue(result.getIsActive());
    }

    @Test
    void entityToGrpcResponse() {

        MentorTimeSlotEntity timeSlotEntity = TestDataGenerator.createTestSlot(
                TestConstantHolder.timeSlotId,
                TestConstantHolder.isActiveTrue,
                Collections.emptySet());

        TimeSlotResponse result = timeSlotMapper.toTimeSlotResponse(
                timeSlotEntity,
                TestConstantHolder.requestId
        );

        Assertions.assertNotNull(result);
        Assertions.assertEquals(TestConstantHolder.requestId, result.getRequestId());
        Assertions.assertEquals(TestConstantHolder.timeSlotId, result.getSlotId());
        Assertions.assertEquals(TestConstantHolder.mentorId, result.getMentorId());
        Assertions.assertEquals(TestConstantHolder.startTimestamp.getSeconds(),
                result.getStartTime().getSeconds()
        );
        Assertions.assertEquals(TestConstantHolder.endTimestamp.getSeconds(),
                result.getEndTime().getSeconds()
        );
        Assertions.assertEquals(SlotType.INDIVIDUAL, result.getSlotType());
        Assertions.assertEquals(SlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        Assertions.assertEquals(
                TestConstantHolder.maxParticipants,
                result.getMaxParticipants()
        );
        Assertions.assertEquals(TestConstantHolder.meetingLink, result.getMeetingLink());
        Assertions.assertEquals(TestConstantHolder.slotDescription, result.getDescription());
        Assertions.assertEquals(TestConstantHolder.createdAtTimestamp.getSeconds(),
                result.getCreatedAt().getSeconds()
        );
    }

    @Test
    void toMentorSlotsInfoResponse_noSlots_empty() {
        List<MentorTimeSlotEntity> emptySlots = Collections.emptyList();

        MentorSlotsInfoResponse response = timeSlotMapper.toMentorSlotsInfoResponse(
                emptySlots,
                TestConstantHolder.requestId
        );

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getSlotsList().isEmpty());
    }

    @Test
    void toMentorSlotsInfoResponse_twoSlots_mixed_twoReturned() {
        long FREE_SLOT_ID = 2L;
        int EXPECTED_SLOTS_SIZE = 2;
        int BOOKED_SLOT_INDEX = TestConstantHolder.zero;
        int FREE_SLOT_INDEX = 1;
        int FIRST_PARTICIPANT_INDEX = TestConstantHolder.zero;
        int EXPECTED_PARTICIPANTS_SIZE = 1;
        String EXPECTED_PARTICIPANT_USERNAME = TestConstantHolder.username;

        Set<UserEntity> participants = new HashSet<>();
        participants.add(TestDataGenerator.getUserEntity());
        MentorTimeSlotEntity bookedSlot = TestDataGenerator.createTestSlot(
                TestConstantHolder.timeSlotId,
                TestConstantHolder.isActiveTrue,
                participants);

        MentorTimeSlotEntity freeSlot = TestDataGenerator.createTestSlot(
                FREE_SLOT_ID,
                TestConstantHolder.isActiveTrue,
                Collections.emptySet());

        List<MentorTimeSlotEntity> slots = Arrays.asList(bookedSlot, freeSlot);

        MentorSlotsInfoResponse response = timeSlotMapper.toMentorSlotsInfoResponse(
                slots,
                TestConstantHolder.requestId
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(EXPECTED_SLOTS_SIZE, response.getSlotsList().size());

        Assertions.assertFalse(
                response.getSlotsList()
                        .get(BOOKED_SLOT_INDEX)
                        .getParticipantsList()
                        .isEmpty());
        Assertions.assertEquals(
                EXPECTED_PARTICIPANTS_SIZE,
                response.getSlotsList()
                        .get(BOOKED_SLOT_INDEX)
                        .getParticipantsList()
                        .size());
        Assertions.assertEquals(
                EXPECTED_PARTICIPANT_USERNAME,
                response.getSlotsList()
                        .get(BOOKED_SLOT_INDEX)
                        .getParticipantsList()
                        .get(FIRST_PARTICIPANT_INDEX)
                        .getUsername()
        );

        Assertions.assertTrue(
                response.getSlotsList()
                        .get(FREE_SLOT_INDEX)
                        .getParticipantsList()
                        .isEmpty());
    }

}