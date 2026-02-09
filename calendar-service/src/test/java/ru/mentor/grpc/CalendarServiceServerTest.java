package ru.mentor.grpc;

import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.mentor.common.BookTimeSlotRequest;
import ru.mentor.common.CancelTimeSlotRequest;
import ru.mentor.common.CancelTimeSlotResponse;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.Header;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.kafka.KafkaFacade;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.mapper.UtilMapper;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestDataGenerator;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CalendarServiceServerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorTimeSlotRepository mentorTimeSlotRepository;

    @Mock
    private BaseMapper baseMapper;

    @Mock
    private KafkaFacade kafkaFacade;

    @Mock
    private TimeSlotMapper timeSlotMapper;

    @Mock
    private UtilMapper utilMapper;

    @InjectMocks
    private CalendarServiceServer calendarServiceServer;

    Long timeSlotId = TestConstantHolder.timeSlotId;
    Long mentorId = TestConstantHolder.mentorId;
    UserEntity mentor = TestDataGenerator.getMentorEntity();
    Long userId = TestConstantHolder.userId;
    UserEntity participantUser = TestDataGenerator.getUserEntity();
    Timestamp startTime = TestConstantHolder.startTimestamp;
    Timestamp endTime = TestConstantHolder.endTimestamp;
    int maxParticipants = TestConstantHolder.maxParticipants;
    String meetingLink = TestConstantHolder.meetingLink;
    String description = TestConstantHolder.slotDescription;
    Timestamp createdAt = TestConstantHolder.createdAtTimestamp;
    String requestId = TestConstantHolder.requestId;
    Header header = TestGrpcStubGenerator.constructHeader();
    private static final int SUCCESS_DELETE = 1;
    private static final int NOT_SUCCESS_DELETE = 0;

    @Test
    void createMentorTimeSlot_Success() {

        CreateTimeSlotRequest request = TestDataGenerator.createTestCreateTimeSlotRequest(
                startTime,
                endTime);

        MentorTimeSlotEntity savedTimeSlot = TestDataGenerator.createTestSlot(
                timeSlotId,
                TestConstantHolder.isActiveTrue,
                Set.of(participantUser));

        TimeSlotResponse response = TestDataGenerator.createTestTimeSlotGrpcResponse(
                startTime,
                endTime,
                createdAt);
        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        Mockito.when(timeSlotMapper.toMentorTimeSlotEntity(request, mentor)).thenReturn(savedTimeSlot);
        Mockito.when(mentorTimeSlotRepository.save(savedTimeSlot)).thenReturn(savedTimeSlot);
        Mockito.when(timeSlotMapper.toTimeSlotResponse(savedTimeSlot, requestId))
                .thenReturn(response);
        Mockito.doNothing().when(responseObserver).onCompleted();

        calendarServiceServer.createMentorTimeSlot(request, responseObserver);

        Mockito.verify(userRepository).findById(mentorId);
        Mockito.verify(timeSlotMapper).toMentorTimeSlotEntity(request, mentor);
        Mockito.verify(mentorTimeSlotRepository).save(savedTimeSlot);
        Mockito.verify(timeSlotMapper).toTimeSlotResponse(savedTimeSlot, requestId);
        Mockito.verify(responseObserver).onCompleted();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(requestId, response.getRequestId());
        Assertions.assertEquals(timeSlotId, response.getSlotId());
        Assertions.assertEquals(mentorId, response.getMentorId());
        Assertions.assertEquals(SlotType.INDIVIDUAL, response.getSlotType());
        Assertions.assertEquals(SlotMeetingType.COMMUNICATION, response.getSlotMeetingType());
        Assertions.assertEquals(maxParticipants, response.getMaxParticipants());
        Assertions.assertEquals(meetingLink, response.getMeetingLink());
        Assertions.assertEquals(description, response.getDescription());
    }

    @Test
    void bookTimeSlot_Success() {

        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                .setHeader(header)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        MentorTimeSlotEntity originTimeSlot = TestDataGenerator.createTestSlot(
                timeSlotId,
                TestConstantHolder.isActiveTrue,
                new HashSet<UserEntity>());

        MentorTimeSlotEntity savedTimeSlot = TestDataGenerator.createTestSlot(
                timeSlotId,
                TestConstantHolder.isActiveTrue,
                Set.of(participantUser));

        TimeSlotResponse response = TestDataGenerator.createTestTimeSlotGrpcResponse(
                startTime,
                endTime,
                createdAt);

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);
        ArgumentCaptor<TimeSlotResponse> responseCaptor = ArgumentCaptor.forClass(TimeSlotResponse.class);

        Mockito.when(userRepository.findByIdOrThrow(userId)).thenReturn(participantUser);
        Mockito.when(mentorTimeSlotRepository.findByIdOrThrow(timeSlotId))
                .thenReturn(originTimeSlot);
        Mockito.when(mentorTimeSlotRepository.saveAndFlush(originTimeSlot))
                .thenReturn(savedTimeSlot);
        Mockito.when(utilMapper.userEntityToUserInfoDto(originTimeSlot.getMentor()))
                .thenReturn(TestEntityStubGenerator.getMentorInfoDto());
        Mockito.when(utilMapper.userEntityToUserInfoDto(participantUser))
                .thenReturn(TestEntityStubGenerator.getUserInfoDto());

        calendarServiceServer.bookTimeslot(request, responseObserver);

        Mockito.verify(userRepository).findByIdOrThrow(userId);
        Mockito.verify(mentorTimeSlotRepository).findByIdOrThrow(timeSlotId);
        Mockito.verify(mentorTimeSlotRepository).saveAndFlush(originTimeSlot);
        Mockito.verify(utilMapper).userEntityToUserInfoDto(originTimeSlot.getMentor());
        Mockito.verify(utilMapper).userEntityToUserInfoDto(participantUser);
        Mockito.verify(responseObserver).onNext(responseCaptor.capture());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(requestId, response.getRequestId());
        Assertions.assertEquals(timeSlotId, response.getSlotId());
        Assertions.assertEquals(mentorId, response.getMentorId());
        Assertions.assertEquals(SlotType.INDIVIDUAL, response.getSlotType());
        Assertions.assertEquals(SlotMeetingType.COMMUNICATION, response.getSlotMeetingType());
        Assertions.assertEquals(maxParticipants, response.getMaxParticipants());
        Assertions.assertEquals(meetingLink, response.getMeetingLink());
        Assertions.assertEquals(description, response.getDescription());
    }

    @Test
    void bookTimeSlot_slotIsAlreadyFull_throwException() {
        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                .setHeader(header)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        MentorTimeSlotEntity originTimeSlot = TestDataGenerator.createTestSlot(
                timeSlotId,
                TestConstantHolder.isActiveTrue,
                new HashSet<UserEntity>());
        originTimeSlot.setMaxParticipants(TestConstantHolder.zero);

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findByIdOrThrow(userId)).thenReturn(participantUser);
        Mockito.when(mentorTimeSlotRepository.findByIdOrThrow(timeSlotId))
                .thenReturn(originTimeSlot);
        calendarServiceServer.bookTimeslot(request, responseObserver);

        Mockito.verify(userRepository).findByIdOrThrow(userId);
        Mockito.verify(mentorTimeSlotRepository).findByIdOrThrow(timeSlotId);
        Mockito.verify(mentorTimeSlotRepository, Mockito.never()).existsOverlappingSlots(
                userId,
                originTimeSlot.getStartTime(),
                originTimeSlot.getEndTime()
        );
        Mockito.verify(mentorTimeSlotRepository, Mockito.never()).save(ArgumentMatchers.any());

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(responseObserver).onError(errorCaptor.capture());
        Mockito.verify(responseObserver, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(responseObserver, Mockito.never()).onCompleted();

        Throwable error = errorCaptor.getValue();
        Assertions.assertInstanceOf(StatusRuntimeException.class, error);
        Assertions.assertEquals(
                Status.UNAVAILABLE.getCode(),
                ((io.grpc.StatusRuntimeException) error).getStatus().getCode()
        );
        Assertions.assertTrue(error.getMessage().contains("На встрече нет свободных мест"));
    }

    @Test
    void bookTimeSlot_slotIsInactive_throwException() {
        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                .setHeader(header)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        MentorTimeSlotEntity originTimeSlot = TestDataGenerator.createTestSlot(
                timeSlotId,
                TestConstantHolder.isActiveFalse,
                new HashSet<UserEntity>());

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findByIdOrThrow(userId)).thenReturn(participantUser);
        Mockito.when(mentorTimeSlotRepository.findByIdOrThrow(timeSlotId))
                .thenReturn(originTimeSlot);
        calendarServiceServer.bookTimeslot(request, responseObserver);

        Mockito.verify(userRepository).findByIdOrThrow(userId);
        Mockito.verify(mentorTimeSlotRepository).findByIdOrThrow(timeSlotId);
        Mockito.verify(mentorTimeSlotRepository, Mockito.never()).existsOverlappingSlots(
                userId,
                originTimeSlot.getStartTime(),
                originTimeSlot.getEndTime()
        );
        Mockito.verify(mentorTimeSlotRepository, Mockito.never()).save(ArgumentMatchers.any());

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(responseObserver).onError(errorCaptor.capture());
        Mockito.verify(responseObserver, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(responseObserver, Mockito.never()).onCompleted();

        Throwable error = errorCaptor.getValue();
        Assertions.assertInstanceOf(StatusRuntimeException.class, error);
        Assertions.assertEquals(
                Status.UNAVAILABLE.getCode(),
                ((io.grpc.StatusRuntimeException) error).getStatus().getCode()
        );
        Assertions.assertTrue(error.getMessage().contains("Слот не активен"));
    }

    @Test
    void bookTimeSlot_existsOverlapping_throwException() {
        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                .setHeader(header)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        MentorTimeSlotEntity originTimeSlot = TestDataGenerator.createTestSlot(
                timeSlotId,
                TestConstantHolder.isActiveTrue,
                new HashSet<UserEntity>());

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findByIdOrThrow(userId)).thenReturn(participantUser);
        Mockito.when(mentorTimeSlotRepository.findByIdOrThrow(timeSlotId))
                .thenReturn(originTimeSlot);
        Mockito.when(mentorTimeSlotRepository.existsOverlappingSlots(
                Mockito.anyLong(),
                Mockito.any(),
                Mockito.any()
        )).thenReturn(true);
        calendarServiceServer.bookTimeslot(request, responseObserver);

        Mockito.verify(userRepository).findByIdOrThrow(userId);
        Mockito.verify(mentorTimeSlotRepository).findByIdOrThrow(timeSlotId);
        Mockito.verify(mentorTimeSlotRepository).existsOverlappingSlots(
                Mockito.eq(userId),
                Mockito.any(),
                Mockito.any()
        );
        Mockito.verify(mentorTimeSlotRepository, Mockito.never()).save(ArgumentMatchers.any());

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(responseObserver).onError(errorCaptor.capture());
        Mockito.verify(responseObserver, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(responseObserver, Mockito.never()).onCompleted();

        Throwable error = errorCaptor.getValue();
        Assertions.assertInstanceOf(StatusRuntimeException.class, error);
        Assertions.assertEquals(
                Status.UNAVAILABLE.getCode(),
                ((io.grpc.StatusRuntimeException) error).getStatus().getCode()
        );
        Assertions.assertTrue(error.getMessage()
                .contains("Вы уже записаны на другой слот в это время"));
    }

    @Test
    void bookTimeSlot_UserNotFound_throwException() {
        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                .setHeader(header)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        MentorTimeSlotEntity originTimeSlot = TestDataGenerator.createTestSlot(
                timeSlotId,
                TestConstantHolder.isActiveTrue,
                new HashSet<UserEntity>());

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findByIdOrThrow(userId)).thenThrow(new EntityNotFoundException(
                String.format(
                        "Юзер с ID = %d не найден",
                        userId
                )
        ));
        calendarServiceServer.bookTimeslot(request, responseObserver);

        Mockito.verify(userRepository).findByIdOrThrow(userId);
        Mockito.verify(mentorTimeSlotRepository, Mockito.never()).findByIdOrThrow(timeSlotId);
        Mockito.verify(mentorTimeSlotRepository, Mockito.never()).existsOverlappingSlots(
                userId,
                originTimeSlot.getStartTime(),
                originTimeSlot.getEndTime()
        );
        Mockito.verify(mentorTimeSlotRepository, Mockito.never()).save(ArgumentMatchers.any());

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(responseObserver).onError(errorCaptor.capture());
        Mockito.verify(responseObserver, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(responseObserver, Mockito.never()).onCompleted();

        Throwable error = errorCaptor.getValue();
        Assertions.assertInstanceOf(StatusRuntimeException.class, error);
        Assertions.assertEquals(
                Status.NOT_FOUND.getCode(),
                ((io.grpc.StatusRuntimeException) error).getStatus().getCode()
        );
        Assertions.assertTrue(error.getMessage().contains(String.format(
                "Юзер с ID = %d не найден",
                userId
        )));
    }

    @Test
    void bookTimeSlot_SlotNotFound_throwException() {
        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                .setHeader(header)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        MentorTimeSlotEntity originTimeSlot = TestDataGenerator.createTestSlot(
                timeSlotId,
                TestConstantHolder.isActiveTrue,
                new HashSet<UserEntity>());

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findByIdOrThrow(userId)).thenReturn(participantUser);
        Mockito.when(mentorTimeSlotRepository.findByIdOrThrow(timeSlotId))
                .thenThrow(new EntityNotFoundException(
                        String.format(
                                "Юзер с ID = %d не найден",
                                userId
                        )
                ));

        calendarServiceServer.bookTimeslot(request, responseObserver);

        Mockito.verify(userRepository).findByIdOrThrow(userId);
        Mockito.verify(mentorTimeSlotRepository).findByIdOrThrow(timeSlotId);
        Mockito.verify(mentorTimeSlotRepository, Mockito.never()).existsOverlappingSlots(
                userId,
                originTimeSlot.getStartTime(),
                originTimeSlot.getEndTime()
        );
        Mockito.verify(mentorTimeSlotRepository, Mockito.never()).save(ArgumentMatchers.any());

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(responseObserver).onError(errorCaptor.capture());
        Mockito.verify(responseObserver, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(responseObserver, Mockito.never()).onCompleted();

        Throwable error = errorCaptor.getValue();
        Assertions.assertInstanceOf(StatusRuntimeException.class, error);
        Assertions.assertEquals(
                Status.NOT_FOUND.getCode(),
                ((io.grpc.StatusRuntimeException) error).getStatus().getCode()
        );
        Assertions.assertTrue(error.getMessage().contains(String.format(
                "Юзер с ID = %d не найден",
                userId
        )));
    }

    @Test
    void cancelTimeSlot_success() {
        CancelTimeSlotRequest cancelRequest = CancelTimeSlotRequest.newBuilder()
                .setHeader(header)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        CancelTimeSlotResponse originResponse = CancelTimeSlotResponse.newBuilder()
                .setRqUid(requestId)
                .build();

        @SuppressWarnings("unchecked")
        StreamObserver<CancelTimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(mentorTimeSlotRepository
                        .deleteParticipantByUserAndSlotIds(timeSlotId, userId))
                .thenReturn(SUCCESS_DELETE);
        Mockito.when(timeSlotMapper
                        .toGrpcCancelTimeSlotResponse(requestId))
                .thenReturn(originResponse);

        calendarServiceServer.cancelTimeSlot(cancelRequest, responseObserver);

        Mockito.verify(mentorTimeSlotRepository).deleteParticipantByUserAndSlotIds(timeSlotId, userId);
        Mockito.verify(timeSlotMapper).toGrpcCancelTimeSlotResponse(requestId);

        ArgumentCaptor<CancelTimeSlotResponse> responseCaptor = ArgumentCaptor.forClass(CancelTimeSlotResponse.class);
        Mockito.verify(responseObserver).onNext(responseCaptor.capture());
        Mockito.verify(responseObserver).onCompleted();

        Assertions.assertEquals(originResponse, responseCaptor.getValue());
    }

    @Test
    void cancelTimeSlot_notPresentOnMeeting_throwException() {
        CancelTimeSlotRequest cancelRequest = CancelTimeSlotRequest.newBuilder()
                .setHeader(header)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        @SuppressWarnings("unchecked")
        StreamObserver<CancelTimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(mentorTimeSlotRepository
                        .deleteParticipantByUserAndSlotIds(timeSlotId, userId))
                .thenReturn(NOT_SUCCESS_DELETE);

        calendarServiceServer.cancelTimeSlot(cancelRequest, responseObserver);

        Mockito.verify(mentorTimeSlotRepository).deleteParticipantByUserAndSlotIds(timeSlotId, userId);

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);

        Mockito.verify(responseObserver).onError(errorCaptor.capture());
        Mockito.verify(responseObserver, Mockito.never()).onNext(Mockito.any());
        Mockito.verify(responseObserver, Mockito.never()).onCompleted();

        StatusRuntimeException exception = (StatusRuntimeException) errorCaptor.getValue();

        Assertions.assertEquals(
                Status.NOT_FOUND.getCode(),
                exception.getStatus().getCode()
        );
    }

    @Test
    void cancelTimeSlot_timeSlotNotFound_throwException() {
        CancelTimeSlotRequest cancelRequest = CancelTimeSlotRequest.newBuilder()
                .setHeader(header)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        @SuppressWarnings("unchecked")
        StreamObserver<CancelTimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(mentorTimeSlotRepository
                        .deleteParticipantByUserAndSlotIds(timeSlotId, userId))
                .thenThrow(new RuntimeException("Ошибка базы данных"));

        calendarServiceServer.cancelTimeSlot(cancelRequest, responseObserver);

        Mockito.verify(mentorTimeSlotRepository).deleteParticipantByUserAndSlotIds(timeSlotId, userId);

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);

        Mockito.verify(responseObserver).onError(errorCaptor.capture());

        StatusRuntimeException exception = (StatusRuntimeException) errorCaptor.getValue();

        Assertions.assertEquals(
                Status.INTERNAL.getCode(),
                exception.getStatus().getCode()
        );
    }

}