package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.facade.CalendarFacade;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class AdminCalendarServiceServerTest {

    @Mock
    private CalendarFacade calendarFacade;

    @InjectMocks
    private AdminCalendarServiceServer adminCalendarServiceServer;

    private GrpcPageRequest grpcPageRequestStub;
    private AllTimeSlotsResponse allTimeSlotsResponse;

    @BeforeEach
    void setUp() {
        grpcPageRequestStub = TestGrpcStubGenerator.constructGrpcPageRequest();
        allTimeSlotsResponse = TestGrpcStubGenerator.constructAllTimeSlotsResponse();
    }

    @Test
    void getAllTimeSlots_emptyRequest_returnsInvalidArgument() {
        StepVerifier.create(adminCalendarServiceServer.getAllTimeSlots(Mono.empty()))
                    .expectErrorSatisfies(error -> {
                        Assertions.assertInstanceOf(StatusRuntimeException.class, error);

                        StatusRuntimeException statusRuntimeException =
                                (StatusRuntimeException) error;

                        Assertions.assertEquals(
                                statusRuntimeException.getStatus().getCode(),
                                Status.INVALID_ARGUMENT.getCode()
                        );

                        Assertions.assertEquals(
                                GrpcErrorText.EMPTY_REQUEST,
                                statusRuntimeException.getStatus().getDescription()
                        );
                    })
                    .verify();

        Mockito.verifyNoInteractions(calendarFacade);
    }

    @Test
    void getAllTimeSlots_validRequest_returnsSuccessResponse() {
        Mockito.when(calendarFacade.findAllTimeSlotsResponseByGrpcPageRequest(grpcPageRequestStub))
                .thenReturn(Mono.just(allTimeSlotsResponse));

        StepVerifier.create(adminCalendarServiceServer.getAllTimeSlots(Mono.just(grpcPageRequestStub)))
                .expectNext(allTimeSlotsResponse)
                .verifyComplete();

        Mockito.verify(calendarFacade, Mockito.times(1))
               .findAllTimeSlotsResponseByGrpcPageRequest(grpcPageRequestStub);
    }

    @Test
    void getAllTimeSlots_entityNotFoundException_returnsNotFoundStatus() {
        Mockito.when(calendarFacade.findAllTimeSlotsResponseByGrpcPageRequest(grpcPageRequestStub))
                .thenReturn(
                        Mono.error(new EntityNotFoundException(
                                TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT)));

        StepVerifier.create(adminCalendarServiceServer.getAllTimeSlots(Mono.just(grpcPageRequestStub)))
                    .expectErrorSatisfies(error -> {
                        Assertions.assertInstanceOf(StatusRuntimeException.class, error);
                        StatusRuntimeException statusRuntimeException = (StatusRuntimeException) error;
                        Assertions.assertEquals(
                                statusRuntimeException.getStatus().getCode(),
                                Status.NOT_FOUND.getCode()
                        );
                        Assertions.assertEquals(
                                TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT,
                                statusRuntimeException.getStatus().getDescription()
                        );
                    })
                .verify();

        Mockito.verify(calendarFacade, Mockito.times(1))
                .findAllTimeSlotsResponseByGrpcPageRequest(grpcPageRequestStub);
    }
}