package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.mapper.UserMapper;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class AdminCalendarServiceServerTest {

    @Mock
    private MentorTimeSlotRepository timeSlotRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private BaseMapper baseMapper;
    @Spy
    private UserMapper userMapper;
    @Spy
    private TimeSlotMapper timeSlotMapper;
    @InjectMocks
    private AdminCalendarServiceServer adminCalendarServiceServer;

    @Test
    void getAllTimeSlots_emptyRequest_returnsInvalidArgument() {
        StepVerifier.create(adminCalendarServiceServer.getAllTimeSlots(Mono.empty()))
                    .expectErrorSatisfies(error -> {
                        Assertions.assertInstanceOf(StatusRuntimeException.class, error);
                        StatusRuntimeException statusException = (StatusRuntimeException) error;
                        Assertions.assertEquals(
                                Status.Code.INVALID_ARGUMENT,
                                statusException.getStatus().getCode()
                        );
                        Assertions.assertEquals(
                                TestConstantHolder.EMPTY_REQUEST_TEXT,
                                statusException.getStatus().getDescription()
                        );
                    })
                    .verify();
    }

    @Test
    void getAllTimeSlots_successfulFlow_returnsResponse() {
        GrpcPageRequest request = TestGrpcStubGenerator.constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(
                TestConstantHolder.PAGE_NUMBER,
                TestConstantHolder.PAGE_SIZE
        );

        MentorTimeSlotEntity slotEntity = TestEntityStubGenerator.constructMentorTimeSlotEntity();
        UserEntity participantEntity = TestEntityStubGenerator.constructParticipantEntity();
        AllTimeSlotsResponse expectedResponse = TestGrpcStubGenerator.constructAllTimeSlotsResponse();

        Mockito.when(timeSlotRepository.findAllBy(pageRequest)).thenReturn(Flux.just(slotEntity));
        Mockito.when(userRepository.findAllSlotParticipantsBySlotId(slotEntity.getId()))
               .thenReturn(Flux.just(participantEntity));
        Mockito.when(timeSlotRepository.count())
               .thenReturn(Mono.just(TestConstantHolder.TOTAL_ELEMENTS_COUNT));

        StepVerifier.create(adminCalendarServiceServer.getAllTimeSlots(Mono.just(request)))
                    .expectNext(expectedResponse)
                    .verifyComplete();
    }

    @Test
    void getAllTimeSlots_entityNotFound_returnsNotFoundStatus() {
        GrpcPageRequest request = TestGrpcStubGenerator.constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(
                TestConstantHolder.PAGE_NUMBER,
                TestConstantHolder.PAGE_SIZE
        );

        Mockito.when(timeSlotRepository.findAllBy(pageRequest))
               .thenReturn(Flux.error(new EntityNotFoundException(TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT)));
        Mockito.when(timeSlotRepository.count()).thenReturn(Mono.just(0L));

        StepVerifier.create(adminCalendarServiceServer.getAllTimeSlots(Mono.just(request)))
                    .expectErrorSatisfies(error -> {
                        Assertions.assertInstanceOf(StatusRuntimeException.class, error);
                        StatusRuntimeException statusException = (StatusRuntimeException) error;
                        Assertions.assertEquals(
                                Status.Code.NOT_FOUND,
                                statusException.getStatus().getCode()
                        );
                        Assertions.assertEquals(
                                TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT,
                                statusException.getStatus().getDescription()
                        );
                    })
                    .verify();
    }

}
