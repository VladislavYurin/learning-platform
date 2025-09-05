package ru.mentor.grpc;

import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.calendar.BookTimeSlotRequest;
import ru.mentor.calendar.CreateTimeSlotRequest;
import ru.mentor.calendar.SlotMeetingType;
import ru.mentor.calendar.SlotType;
import ru.mentor.calendar.TimeSlotResponse;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.constant.Role;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CalendarServiceServerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorTimeSlotRepository mentorTimeSlotRepository;

    @Spy
    private TimeSlotMapper timeSlotMapper;

    @InjectMocks
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
                        .toEpochSecond(ZoneOffset.UTC)).build();
        Timestamp endTime = Timestamp.newBuilder()
                .setSeconds(LocalDateTime.of(2025, 1, 15, 14, 0)
                        .toEpochSecond(ZoneOffset.UTC)).build();

        CreateTimeSlotRequest request = CreateTimeSlotRequest.newBuilder()
                .setRqUid(requestUUID)
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
                .startTime(LocalDateTime.of(2025, 1, 15, 13, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 14, 0))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(maxParticipants)
                .meetingLink(meetingLink)
                .description(description)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findById(mentorId)).thenReturn(Optional.of(mentorUser));
        Mockito.when(mentorTimeSlotRepository.save(any(MentorTimeSlotEntity.class)))
                .thenReturn(savedTimeSlot);

        // When
        calendarServiceServer.createMentorTimeSlot(request, responseObserver);

        // Then
        verify(userRepository).findById(mentorId);
        verify(mentorTimeSlotRepository).save(any(MentorTimeSlotEntity.class));

        ArgumentCaptor<TimeSlotResponse> responseCaptor = ArgumentCaptor.forClass(TimeSlotResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        TimeSlotResponse response = responseCaptor.getValue();
        assertNotNull(response);
        assertEquals(requestUUID, response.getRqUid());
        assertEquals(mentorId, response.getSlotId());
        assertEquals(mentorId, response.getMentorId());
        assertEquals(SlotType.INDIVIDUAL, response.getSlotType());
        assertEquals(SlotMeetingType.COMMUNICATION, response.getSlotMeetingType());
        assertEquals(maxParticipants, response.getMaxParticipants());
        assertEquals(meetingLink, response.getMeetingLink());
        assertEquals(description, response.getDescription());
    }

    @Test
    void bookTimeSlot_Success() {

        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                .setRqUid(requestUUID)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        MentorTimeSlotEntity originTimeSlot = MentorTimeSlotEntity.builder()
                .id(1L)
                .mentor(mentorUser)
                .startTime(LocalDateTime.of(2025, 1, 15, 13, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 14, 0))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
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
                .startTime(LocalDateTime.of(2025, 1, 15, 13, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 14, 0))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(maxParticipants)
                .meetingLink(meetingLink)
                .description(description)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .meetingParticipants(Set.of(testUser))
                .build();

        @SuppressWarnings("unchecked")
        StreamObserver<TimeSlotResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Mockito.when(userRepository.findByIdOrThrow(userId)).thenReturn(testUser);
        Mockito.when(mentorTimeSlotRepository.findByIdOrThrow(timeSlotId)).thenReturn(originTimeSlot);
        Mockito.when(mentorTimeSlotRepository.existsOverlappingSlots(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);
        Mockito.when(mentorTimeSlotRepository.save(any(MentorTimeSlotEntity.class))).thenReturn(savedTimeSlot);

        // When
        calendarServiceServer.bookTimeslot(request, responseObserver);

        // Then
        verify(userRepository).findByIdOrThrow(userId);
        verify(mentorTimeSlotRepository).findByIdOrThrow(timeSlotId);
        verify(mentorTimeSlotRepository).existsOverlappingSlots(userId, originTimeSlot.getStartTime(), originTimeSlot.getEndTime());

        ArgumentCaptor<TimeSlotResponse> responseCaptor = ArgumentCaptor.forClass(TimeSlotResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        TimeSlotResponse response = responseCaptor.getValue();
        assertNotNull(response);
        assertEquals(requestUUID, response.getRqUid());
        assertEquals(1L, response.getSlotId());
        assertEquals(mentorId, response.getMentorId());
        assertEquals(SlotType.INDIVIDUAL, response.getSlotType());
        assertEquals(SlotMeetingType.COMMUNICATION, response.getSlotMeetingType());
        assertEquals(maxParticipants, response.getMaxParticipants());
        assertEquals(meetingLink, response.getMeetingLink());
        assertEquals(description, response.getDescription());


    }

    @Test
    void bookTimeSlot_slotIsAlreadyFull_throwException(){
        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                .setRqUid(requestUUID)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        MentorTimeSlotEntity originTimeSlot = MentorTimeSlotEntity.builder()
                .id(1L)
                .mentor(mentorUser)
                .startTime(LocalDateTime.of(2025, 1, 15, 13, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 14, 0))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
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
        Mockito.when(mentorTimeSlotRepository.findByIdOrThrow(timeSlotId)).thenReturn(originTimeSlot);
        // When
        calendarServiceServer.bookTimeslot(request, responseObserver);

        // Then
        verify(userRepository).findByIdOrThrow(userId);
        verify(mentorTimeSlotRepository).findByIdOrThrow(timeSlotId);
        verify(mentorTimeSlotRepository, never()).existsOverlappingSlots(userId, originTimeSlot.getStartTime(), originTimeSlot.getEndTime());
        verify(mentorTimeSlotRepository, never()).save(any());

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver).onError(errorCaptor.capture());
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();

        Throwable error = errorCaptor.getValue();
        assertInstanceOf(StatusRuntimeException.class, error);
        assertEquals(Status.UNAVAILABLE.getCode(), ((io.grpc.StatusRuntimeException) error).getStatus().getCode());
        assertTrue(error.getMessage().contains("На встрече нет свободных мест"));

    }

    @Test
    void bookTimeSlot_slotIsInactive_throwException(){
        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                .setRqUid(requestUUID)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        MentorTimeSlotEntity originTimeSlot = MentorTimeSlotEntity.builder()
                .id(1L)
                .mentor(mentorUser)
                .startTime(LocalDateTime.of(2025, 1, 15, 13, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 14, 0))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
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
        Mockito.when(mentorTimeSlotRepository.findByIdOrThrow(timeSlotId)).thenReturn(originTimeSlot);
        // When
        calendarServiceServer.bookTimeslot(request, responseObserver);

        // Then
        verify(userRepository).findByIdOrThrow(userId);
        verify(mentorTimeSlotRepository).findByIdOrThrow(timeSlotId);
        verify(mentorTimeSlotRepository, never()).existsOverlappingSlots(userId, originTimeSlot.getStartTime(), originTimeSlot.getEndTime());
        verify(mentorTimeSlotRepository, never()).save(any());

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver).onError(errorCaptor.capture());
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();

        Throwable error = errorCaptor.getValue();
        assertInstanceOf(StatusRuntimeException.class, error);
        assertEquals(Status.UNAVAILABLE.getCode(), ((io.grpc.StatusRuntimeException) error).getStatus().getCode());
        assertTrue(error.getMessage().contains("Слот не активен"));

    }

    @Test
    void bookTimeSlot_existsOverlapping_throwException(){
        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                .setRqUid(requestUUID)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        MentorTimeSlotEntity originTimeSlot = MentorTimeSlotEntity.builder()
                .id(1L)
                .mentor(mentorUser)
                .startTime(LocalDateTime.of(2025, 1, 15, 13, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 14, 0))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
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
        Mockito.when(mentorTimeSlotRepository.findByIdOrThrow(timeSlotId)).thenReturn(originTimeSlot);
        Mockito.when(mentorTimeSlotRepository.existsOverlappingSlots(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);
        // When
        calendarServiceServer.bookTimeslot(request, responseObserver);

        // Then
        verify(userRepository).findByIdOrThrow(userId);
        verify(mentorTimeSlotRepository).findByIdOrThrow(timeSlotId);
        verify(mentorTimeSlotRepository).existsOverlappingSlots(userId, originTimeSlot.getStartTime(), originTimeSlot.getEndTime());
        verify(mentorTimeSlotRepository, never()).save(any());

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver).onError(errorCaptor.capture());
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();

        Throwable error = errorCaptor.getValue();
        assertInstanceOf(StatusRuntimeException.class, error);
        assertEquals(Status.UNAVAILABLE.getCode(), ((io.grpc.StatusRuntimeException) error).getStatus().getCode());
        assertTrue(error.getMessage().contains("Вы уже записаны на другой слот в это время"));
    }

    @Test
    void bookTimeSlot_UserNotFound_throwException(){
        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                .setRqUid(requestUUID)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        MentorTimeSlotEntity originTimeSlot = MentorTimeSlotEntity.builder()
                .id(1L)
                .mentor(mentorUser)
                .startTime(LocalDateTime.of(2025, 1, 15, 13, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 14, 0))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
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
        // When
        calendarServiceServer.bookTimeslot(request, responseObserver);

        // Then
        verify(userRepository).findByIdOrThrow(userId);
        verify(mentorTimeSlotRepository, never()).findByIdOrThrow(timeSlotId);
        verify(mentorTimeSlotRepository, never()).existsOverlappingSlots(userId, originTimeSlot.getStartTime(), originTimeSlot.getEndTime());
        verify(mentorTimeSlotRepository, never()).save(any());

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver).onError(errorCaptor.capture());
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();

        Throwable error = errorCaptor.getValue();
        assertInstanceOf(StatusRuntimeException.class, error);
        assertEquals(Status.NOT_FOUND.getCode(), ((io.grpc.StatusRuntimeException) error).getStatus().getCode());
        assertTrue(error.getMessage().contains(String.format(
                "Юзер с ID = %d не найден",
                userId
        )));
    }

    @Test
    void bookTimeSlot_SlotNotFound_throwException(){
        BookTimeSlotRequest request = BookTimeSlotRequest.newBuilder()
                .setRqUid(requestUUID)
                .setSlotId(timeSlotId)
                .setUserId(userId)
                .build();

        MentorTimeSlotEntity originTimeSlot = MentorTimeSlotEntity.builder()
                .id(1L)
                .mentor(mentorUser)
                .startTime(LocalDateTime.of(2025, 1, 15, 13, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 14, 0))
                .slotType(CalendarSlotType.INDIVIDUAL)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
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
        Mockito.when(mentorTimeSlotRepository.findByIdOrThrow(timeSlotId)).thenThrow(new EntityNotFoundException(
                String.format(
                        "Юзер с ID = %d не найден",
                        userId
                )
        ));

        // When
        calendarServiceServer.bookTimeslot(request, responseObserver);

        // Then
        verify(userRepository).findByIdOrThrow(userId);
        verify(mentorTimeSlotRepository).findByIdOrThrow(timeSlotId);
        verify(mentorTimeSlotRepository, never()).existsOverlappingSlots(userId, originTimeSlot.getStartTime(), originTimeSlot.getEndTime());
        verify(mentorTimeSlotRepository, never()).save(any());

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver).onError(errorCaptor.capture());
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();

        Throwable error = errorCaptor.getValue();
        assertInstanceOf(StatusRuntimeException.class, error);
        assertEquals(Status.NOT_FOUND.getCode(), ((io.grpc.StatusRuntimeException) error).getStatus().getCode());
        assertTrue(error.getMessage().contains(String.format(
                "Юзер с ID = %d не найден",
                userId
        )));
    }
}