package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.common.CreateTimeSlotRequest;
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
import ru.mentor.grpc.HeaderFactory;
import ru.mentor.testUtil.TestDataGenerator;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class TimeSlotMapperTest {

    @Mock
    private HeaderFactory headerFactory;

    @InjectMocks
    TimeSlotMapper timeSlotMapper;

    @Test
    void grpcResponseToDto() {
        Timestamp startTime = UtilMapper.buildTimestamp(
                LocalDateTime.of(2025, 1, 15, 13, 0));
        Timestamp endTime = UtilMapper.buildTimestamp(
                LocalDateTime.of(2025, 1, 15, 14, 0));
        Timestamp createdAt = UtilMapper.buildTimestamp(
                LocalDateTime.of(2025, 1, 14, 10, 0));

        TimeSlotResponse grpcResponse = TestDataGenerator.createTestTimeSlotGrpcResponse(
                startTime, endTime, createdAt);

        MentorTimeSlotDto result = timeSlotMapper.grpcResponseToDto(grpcResponse);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(TestDataGenerator.TEST_UUID, result.getRqUId());
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(1L, result.getMentorId());
        Assertions.assertEquals(LocalDateTime.of(2025, 1, 15, 13, 0), result.getStartTime());
        Assertions.assertEquals(LocalDateTime.of(2025, 1, 15, 14, 0), result.getEndTime());
        Assertions.assertEquals(CalendarSlotType.INDIVIDUAL, result.getSlotType());
        Assertions.assertEquals(CalendarSlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        Assertions.assertEquals(TestDataGenerator.TEST_MAX_PARTICIPANTS, result.getMaxParticipants());
        Assertions.assertEquals(TestDataGenerator.TEST_LINK, result.getMeetingLink());
        Assertions.assertEquals(TestDataGenerator.TEST_DESCRIPTION, result.getDescription());
        Assertions.assertEquals(LocalDateTime.of(2025, 1, 14, 10, 0), result.getCreatedAt());
    }

    @Test
    void requestCreateToGrpcDto() {
        Mockito.when(headerFactory.create(Mockito.anyString()))
                .thenAnswer(inv -> ru.mentor.common.Header.newBuilder()
                        .setRequestId(inv.getArgument(0, String.class))
                        .setNodeId("test-node")
                        .setApiKey("test-api")
                        .build());

        LocalDateTime startTime = LocalDateTime.of(2025, 1, 15, 13, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 1, 15, 14, 0);

        MentorTimeSlotCreateRequest createRequest = TestDataGenerator.createTestMentorTimeSlotCreateRequest(
                startTime, endTime);

        UserEntity testMentorUser = TestDataGenerator.getTestMentorUser();
        CreateTimeSlotRequest result = timeSlotMapper.requestCreateToGrpcDto(createRequest, TestDataGenerator.TEST_UUID, testMentorUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(TestDataGenerator.TEST_UUID, result.getHeader().getRequestId());
        Assertions.assertEquals(1L, result.getMentorId());
        Assertions.assertEquals(LocalDateTime.of(2025, 1, 15, 13, 0)
                        .toEpochSecond(ZoneOffset.UTC),
                result.getStartTime().getSeconds());
        Assertions.assertEquals(LocalDateTime.of(2025, 1, 15, 14, 0)
                        .toEpochSecond(ZoneOffset.UTC),
                result.getEndTime().getSeconds());
        Assertions.assertEquals(SlotType.INDIVIDUAL, result.getSlotType());
        Assertions.assertEquals(SlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        Assertions.assertEquals(TestDataGenerator.TEST_MAX_PARTICIPANTS, result.getMaxParticipants());
        Assertions.assertEquals(TestDataGenerator.TEST_LINK, result.getMeetingLink());
        Assertions.assertEquals(TestDataGenerator.TEST_DESCRIPTION, result.getDescription());
    }

    @Test
    void grpcCreateRequestToEntity() {
        Timestamp startTime = UtilMapper.buildTimestamp(
                LocalDateTime.of(2025, 1, 15, 13, 0));
        Timestamp endTime = UtilMapper.buildTimestamp(
                LocalDateTime.of(2025, 1, 15, 14, 0));

        CreateTimeSlotRequest request = TestDataGenerator.createTestCreateTimeSlotRequest(
                startTime, endTime);
        UserEntity testMentorUser = TestDataGenerator.getTestMentorUser();
        MentorTimeSlotEntity result = timeSlotMapper.grpcCreateRequestToEntity(request, testMentorUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testMentorUser, result.getMentor());
        Assertions.assertEquals(LocalDateTime.of(2025, 1, 15, 13, 0), result.getStartTime());
        Assertions.assertEquals(LocalDateTime.of(2025, 1, 15, 14, 0), result.getEndTime());
        Assertions.assertEquals(CalendarSlotType.INDIVIDUAL, result.getSlotType());
        Assertions.assertEquals(CalendarSlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        Assertions.assertEquals(TestDataGenerator.TEST_MAX_PARTICIPANTS, result.getMaxParticipants());
        Assertions.assertEquals(TestDataGenerator.TEST_LINK, result.getMeetingLink());
        Assertions.assertEquals(TestDataGenerator.TEST_DESCRIPTION, result.getDescription());
        Assertions.assertTrue(result.getIsActive());
    }

    @Test
    void entityToGrpcResponse() {

        MentorTimeSlotEntity timeSlotEntity = TestDataGenerator.createTestSlot(
                1L, Collections.emptySet(), true);

        TimeSlotResponse result = timeSlotMapper.entityToGrpcResponse(timeSlotEntity, TestDataGenerator.TEST_UUID);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(TestDataGenerator.TEST_UUID, result.getRequestId());
        Assertions.assertEquals(1L, result.getSlotId());
        Assertions.assertEquals(1L, result.getMentorId());
        Assertions.assertEquals(LocalDateTime.of(2025, 1, 15, 13, 0)
                .toEpochSecond(ZoneOffset.UTC), result.getStartTime().getSeconds());
        Assertions.assertEquals(LocalDateTime.of(2025, 1, 15, 14, 0)
                .toEpochSecond(ZoneOffset.UTC), result.getEndTime().getSeconds());
        Assertions.assertEquals(SlotType.INDIVIDUAL, result.getSlotType());
        Assertions.assertEquals(SlotMeetingType.COMMUNICATION, result.getSlotMeetingType());
        Assertions.assertEquals(TestDataGenerator.TEST_MAX_PARTICIPANTS, result.getMaxParticipants());
        Assertions.assertEquals(TestDataGenerator.TEST_LINK, result.getMeetingLink());
        Assertions.assertEquals(TestDataGenerator.TEST_DESCRIPTION, result.getDescription());
        Assertions.assertEquals(LocalDateTime.of(2025, 1, 14, 10, 0)
                .toEpochSecond(ZoneOffset.UTC), result.getCreatedAt().getSeconds());
    }

    @Test
    void toMentorSlotsInfoResponse_noSlots_empty() {
        List<MentorTimeSlotEntity> emptySlots = Collections.emptyList();
        String rqUId = "test-rq-id";

        MentorSlotsInfoResponse response = timeSlotMapper.convertToMentorSlotsInfoResponse(emptySlots, rqUId);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getSlotsList().isEmpty());
    }

    @Test
    void toMentorSlotsInfoResponse_twoSlots_mixed_twoReturned() {
        String rqUId = "test-rq-id";

        Set<UserEntity> participants = new HashSet<>();
        participants.add(TestDataGenerator.getTestParticipantUser());
        MentorTimeSlotEntity bookedSlot = TestDataGenerator.createTestSlot(1L, participants, true);

        MentorTimeSlotEntity freeSlot = TestDataGenerator.createTestSlot(2L, Collections.emptySet(), true);

        List<MentorTimeSlotEntity> slots = Arrays.asList(bookedSlot, freeSlot);

        MentorSlotsInfoResponse response = timeSlotMapper.convertToMentorSlotsInfoResponse(slots, rqUId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.getSlotsList().size());

        Assertions.assertFalse(response.getSlotsList().get(0).getParticipantsList().isEmpty());
        Assertions.assertEquals(1, response.getSlotsList().get(0).getParticipantsList().size());
        Assertions.assertEquals("participant", response.getSlotsList().get(0).getParticipantsList().get(0).getUsername());

        Assertions.assertTrue(response.getSlotsList().get(1).getParticipantsList().isEmpty());
    }
}