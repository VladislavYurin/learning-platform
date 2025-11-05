package ru.mentor.grpc;

import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.mentor.common.BookTimeSlotRequest;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.constant.Role;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.kafka.KafkaFacade;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CalendarServiceServerTest {

    @Mock
    private HeaderFactory headerFactory;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorTimeSlotRepository mentorTimeSlotRepository;

    @Mock
    private KafkaFacade kafkaFacade;

    private BaseMapper baseMapper;
    private TimeSlotMapper timeSlotMapper;
    private CalendarServiceServer calendarServiceServer;

    String requestUUID = UUID.randomUUID().toString();
    int maxParticipants = 10;
    String meetingLink = "https://meet.com/test";
    String description = "Test description";
    Long mentorId = 1L;
    Long userId = 2L;
    UserEntity mentorUser = new UserEntity();
    UserEntity testUser = new UserEntity();
    MentorTimeSlotEntity mentorTimeSlot = new MentorTimeSlotEntity();
    Long timeSlotId = 1L;

    @BeforeEach
    void setUp() {
        Mockito.lenient()
                .when(headerFactory.create(Mockito.anyString()))
                .thenAnswer(inv -> ru.mentor.common.Header.newBuilder()
                        .setRequestId(inv.getArgument(0, String.class))  // <- тот rqUid, который передаст код
                        .setNodeId("test-node")
                        .setApiKey("test-api")
                        .build());

        timeSlotMapper = Mockito.spy(new TimeSlotMapper(headerFactory));

        baseMapper = Mockito.spy(new BaseMapper(headerFactory));

        calendarServiceServer = new CalendarServiceServer(
                timeSlotMapper,
                userRepository,
                mentorTimeSlotRepository,
                baseMapper,
                kafkaFacade
        );

        mentorUser = UserEntity.builder()
                               .id(mentorId)
                               .username("mentor")
                               .password("password")
                               .role(Role.MENTOR)
                               .firstName("John")
                               .lastName("Doe")
                               .tgNickname("johndoe")
                               .build();

        testUser = UserEntity.builder()
                             .id(userId)
                             .username("user")
                             .password("password1")
                             .role(Role.USER)
                             .firstName("Test")
                             .lastName("User")
                             .tgNickname("testuser")
                             .build();

    }

    @Test
    void createMentorTimeSlot_Success() {
        Timestamp startTime = Timestamp.newBuilder()
                                       .setSeconds(LocalDateTime.of(2025, 1, 15, 13, 0)
                                                                .toEpochSecond(ZoneOffset.UTC))
                                       .build();
        Timestamp endTime = Timestamp.newBuilder()
                                     .setSeconds(LocalDateTime.of(2025, 1, 15, 14, 0)
                                                              .toEpochSecond(ZoneOffset.UTC))
                                     .build();

        CreateTimeSlotRequest request = CreateTimeSlotRequest.newBuilder()
                                                             .setHeader(headerFactory.create(requestUUID))
                                                             .setMentorId(mentorId)
                                                             .setStartTime(startTime)
                                                             .setEndTime(endTime)
                                                             .setSlotType(SlotType.INDIVIDUAL)
                                                             .setSlotMeetingType(SlotMeetingType.COMMUNICATION)
                                                             .setMaxParticipants(maxParticipants)
                                                             .setMeetingLink(meetingLink)
                                                             .setDescription(description)
                                                             .build();

        MentorTimeSlotEntity savedTimeSlot = MentorTimeSlotEntity.builder()
                                                                 .id(mentorId)
                                                                 .mentor(mentorUser)
                                                                 .startTime(LocalDateTime.of(
                                                                         2025,
                                                                         1,
                                                                         15,
                                                                         13,
                                                                         0
                                                                 ))
                                                                 .endTime(LocalDateTime.of(
                                                                         2025,
                                                                         1,
                                                                         15,
                                                                         14,
                                                                         0
                                                                 ))
                                                                 .slotType(CalendarSlotType.INDIVIDUAL)
                                                                 .slotMeetingType(
                                                                         CalendarSlotMeetingType.COMMUNICATION)
                                                                 .maxParticipants(maxParticipants)
                                                                 .meetingLink(meetingLink)
                                                                 .description(description)
                                                                 .isActive(true)
                                                                 .createdAt(LocalDateTime.now())
                                                                 .build();

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findById(mentorId)).thenReturn(Optional.of(mentorUser));
        Mockito.when(mentorTimeSlotRepository.save(ArgumentMatchers.any(MentorTimeSlotEntity.class)))
               .thenReturn(savedTimeSlot);

        calendarServiceServer.createMentorTimeSlot(request, responseObserver);

        Mockito.verify(userRepository).findById(mentorId);
        Mockito.verify(mentorTimeSlotRepository)
               .save(ArgumentMatchers.any(MentorTimeSlotEntity.class));

        ArgumentCaptor<TimeSlotResponse> responseCaptor = ArgumentCaptor.forClass(TimeSlotResponse.class);
        Mockito.verify(responseObserver).onNext(responseCaptor.capture());
        Mockito.verify(responseObserver).onCompleted();

        TimeSlotResponse response = responseCaptor.getValue();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(requestUUID, response.getRequestId());
        Assertions.assertEquals(mentorId, response.getSlotId());
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
                                                         .setHeader(headerFactory.create(requestUUID))
                                                         .setSlotId(timeSlotId)
                                                         .setUserId(userId)
                                                         .build();

        MentorTimeSlotEntity originTimeSlot = MentorTimeSlotEntity.builder()
                                                                  .id(1L)
                                                                  .mentor(mentorUser)
                                                                  .startTime(LocalDateTime.of(
                                                                          2025,
                                                                          1,
                                                                          15,
                                                                          13,
                                                                          0
                                                                  ))
                                                                  .endTime(LocalDateTime.of(
                                                                          2025,
                                                                          1,
                                                                          15,
                                                                          14,
                                                                          0
                                                                  ))
                                                                  .slotType(CalendarSlotType.INDIVIDUAL)
                                                                  .slotMeetingType(
                                                                          CalendarSlotMeetingType.COMMUNICATION)
                                                                  .maxParticipants(maxParticipants)
                                                                  .meetingParticipants(new HashSet<UserEntity>())
                                                                  .meetingLink(meetingLink)
                                                                  .description(description)
                                                                  .isActive(true)
                                                                  .createdAt(LocalDateTime.now())
                                                                  .build();

        MentorTimeSlotEntity savedTimeSlot = MentorTimeSlotEntity.builder()
                                                                 .id(1L)
                                                                 .mentor(mentorUser)
                                                                 .startTime(LocalDateTime.of(
                                                                         2025,
                                                                         1,
                                                                         15,
                                                                         13,
                                                                         0
                                                                 ))
                                                                 .endTime(LocalDateTime.of(
                                                                         2025,
                                                                         1,
                                                                         15,
                                                                         14,
                                                                         0
                                                                 ))
                                                                 .slotType(CalendarSlotType.INDIVIDUAL)
                                                                 .slotMeetingType(
                                                                         CalendarSlotMeetingType.COMMUNICATION)
                                                                 .maxParticipants(maxParticipants)
                                                                 .meetingLink(meetingLink)
                                                                 .description(description)
                                                                 .isActive(true)
                                                                 .createdAt(LocalDateTime.now())
                                                                 .meetingParticipants(Set.of(
                                                                         testUser))
                                                                 .build();

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findByIdOrThrow(userId)).thenReturn(testUser);
        Mockito.when(mentorTimeSlotRepository.findByIdOrThrow(timeSlotId))
               .thenReturn(originTimeSlot);
        Mockito.when(mentorTimeSlotRepository.existsOverlappingSlots(
                Mockito.anyLong(),
                ArgumentMatchers.any(LocalDateTime.class),
                ArgumentMatchers.any(LocalDateTime.class)
        )).thenReturn(false);
        Mockito.when(mentorTimeSlotRepository.saveAndFlush(ArgumentMatchers.any(MentorTimeSlotEntity.class)))
               .thenReturn(savedTimeSlot);

        calendarServiceServer.bookTimeslot(request, responseObserver);

        Mockito.verify(userRepository).findByIdOrThrow(userId);
        Mockito.verify(mentorTimeSlotRepository).findByIdOrThrow(timeSlotId);
        Mockito.verify(mentorTimeSlotRepository).existsOverlappingSlots(
                userId,
                originTimeSlot.getStartTime(),
                originTimeSlot.getEndTime()
        );

        ArgumentCaptor<TimeSlotResponse> responseCaptor = ArgumentCaptor.forClass(TimeSlotResponse.class);
        Mockito.verify(responseObserver).onNext(responseCaptor.capture());
        Mockito.verify(responseObserver).onCompleted();

        TimeSlotResponse response = responseCaptor.getValue();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(requestUUID, response.getRequestId());
        Assertions.assertEquals(1L, response.getSlotId());
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
                                                         .setHeader(headerFactory.create(requestUUID))
                                                         .setSlotId(timeSlotId)
                                                         .setUserId(userId)
                                                         .build();

        MentorTimeSlotEntity originTimeSlot = MentorTimeSlotEntity.builder()
                                                                  .id(1L)
                                                                  .mentor(mentorUser)
                                                                  .startTime(LocalDateTime.of(
                                                                          2025,
                                                                          1,
                                                                          15,
                                                                          13,
                                                                          0
                                                                  ))
                                                                  .endTime(LocalDateTime.of(
                                                                          2025,
                                                                          1,
                                                                          15,
                                                                          14,
                                                                          0
                                                                  ))
                                                                  .slotType(CalendarSlotType.INDIVIDUAL)
                                                                  .slotMeetingType(
                                                                          CalendarSlotMeetingType.COMMUNICATION)
                                                                  .maxParticipants(0)
                                                                  .meetingParticipants(new HashSet<UserEntity>())
                                                                  .meetingLink(meetingLink)
                                                                  .description(description)
                                                                  .isActive(true)
                                                                  .createdAt(LocalDateTime.now())
                                                                  .build();

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findByIdOrThrow(userId)).thenReturn(testUser);
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
                                                         .setHeader(headerFactory.create(requestUUID))
                                                         .setSlotId(timeSlotId)
                                                         .setUserId(userId)
                                                         .build();

        MentorTimeSlotEntity originTimeSlot = MentorTimeSlotEntity.builder()
                                                                  .id(1L)
                                                                  .mentor(mentorUser)
                                                                  .startTime(LocalDateTime.of(
                                                                          2025,
                                                                          1,
                                                                          15,
                                                                          13,
                                                                          0
                                                                  ))
                                                                  .endTime(LocalDateTime.of(
                                                                          2025,
                                                                          1,
                                                                          15,
                                                                          14,
                                                                          0
                                                                  ))
                                                                  .slotType(CalendarSlotType.INDIVIDUAL)
                                                                  .slotMeetingType(
                                                                          CalendarSlotMeetingType.COMMUNICATION)
                                                                  .maxParticipants(1)
                                                                  .meetingParticipants(new HashSet<UserEntity>())
                                                                  .meetingLink(meetingLink)
                                                                  .description(description)
                                                                  .isActive(false)
                                                                  .createdAt(LocalDateTime.now())
                                                                  .build();

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findByIdOrThrow(userId)).thenReturn(testUser);
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
                                                         .setHeader(headerFactory.create(requestUUID))
                                                         .setSlotId(timeSlotId)
                                                         .setUserId(userId)
                                                         .build();

        MentorTimeSlotEntity originTimeSlot = MentorTimeSlotEntity.builder()
                                                                  .id(1L)
                                                                  .mentor(mentorUser)
                                                                  .startTime(LocalDateTime.of(
                                                                          2025,
                                                                          1,
                                                                          15,
                                                                          13,
                                                                          0
                                                                  ))
                                                                  .endTime(LocalDateTime.of(
                                                                          2025,
                                                                          1,
                                                                          15,
                                                                          14,
                                                                          0
                                                                  ))
                                                                  .slotType(CalendarSlotType.INDIVIDUAL)
                                                                  .slotMeetingType(
                                                                          CalendarSlotMeetingType.COMMUNICATION)
                                                                  .maxParticipants(1)
                                                                  .meetingParticipants(new HashSet<UserEntity>())
                                                                  .meetingLink(meetingLink)
                                                                  .description(description)
                                                                  .isActive(true)
                                                                  .createdAt(LocalDateTime.now())
                                                                  .build();

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findByIdOrThrow(userId)).thenReturn(testUser);
        Mockito.when(mentorTimeSlotRepository.findByIdOrThrow(timeSlotId))
               .thenReturn(originTimeSlot);
        Mockito.when(mentorTimeSlotRepository.existsOverlappingSlots(
                Mockito.anyLong(),
                ArgumentMatchers.any(LocalDateTime.class),
                ArgumentMatchers.any(LocalDateTime.class)
        )).thenReturn(true);
        calendarServiceServer.bookTimeslot(request, responseObserver);

        Mockito.verify(userRepository).findByIdOrThrow(userId);
        Mockito.verify(mentorTimeSlotRepository).findByIdOrThrow(timeSlotId);
        Mockito.verify(mentorTimeSlotRepository).existsOverlappingSlots(
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
        Assertions.assertTrue(error.getMessage()
                                   .contains("Вы уже записаны на другой слот в это время"));
    }

    @Test
    void bookTimeSlot_UserNotFound_throwException() {
        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                                                         .setHeader(headerFactory.create(requestUUID))
                                                         .setSlotId(timeSlotId)
                                                         .setUserId(userId)
                                                         .build();

        MentorTimeSlotEntity originTimeSlot = MentorTimeSlotEntity.builder()
                                                                  .id(1L)
                                                                  .mentor(mentorUser)
                                                                  .startTime(LocalDateTime.of(
                                                                          2025,
                                                                          1,
                                                                          15,
                                                                          13,
                                                                          0
                                                                  ))
                                                                  .endTime(LocalDateTime.of(
                                                                          2025,
                                                                          1,
                                                                          15,
                                                                          14,
                                                                          0
                                                                  ))
                                                                  .slotType(CalendarSlotType.INDIVIDUAL)
                                                                  .slotMeetingType(
                                                                          CalendarSlotMeetingType.COMMUNICATION)
                                                                  .maxParticipants(1)
                                                                  .meetingParticipants(new HashSet<UserEntity>())
                                                                  .meetingLink(meetingLink)
                                                                  .description(description)
                                                                  .isActive(true)
                                                                  .createdAt(LocalDateTime.now())
                                                                  .build();

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
                                                         .setHeader(headerFactory.create(requestUUID))
                                                         .setSlotId(timeSlotId)
                                                         .setUserId(userId)
                                                         .build();

        MentorTimeSlotEntity originTimeSlot = MentorTimeSlotEntity.builder()
                                                                  .id(1L)
                                                                  .mentor(mentorUser)
                                                                  .startTime(LocalDateTime.of(
                                                                          2025,
                                                                          1,
                                                                          15,
                                                                          13,
                                                                          0
                                                                  ))
                                                                  .endTime(LocalDateTime.of(
                                                                          2025,
                                                                          1,
                                                                          15,
                                                                          14,
                                                                          0
                                                                  ))
                                                                  .slotType(CalendarSlotType.INDIVIDUAL)
                                                                  .slotMeetingType(
                                                                          CalendarSlotMeetingType.COMMUNICATION)
                                                                  .maxParticipants(1)
                                                                  .meetingParticipants(new HashSet<UserEntity>())
                                                                  .meetingLink(meetingLink)
                                                                  .description(description)
                                                                  .isActive(true)
                                                                  .createdAt(LocalDateTime.now())
                                                                  .build();

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findByIdOrThrow(userId)).thenReturn(testUser);
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

}