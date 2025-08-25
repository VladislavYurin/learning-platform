package ru.mentor.grpc;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mentor.admin.AllTimeSlotsResponse;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;


@ExtendWith(MockitoExtension.class)
public class AdminCalendarServiceServerTest {

    @Spy
    private BaseMapper baseMapper;
    @Mock
    private MentorTimeSlotRepository repository;
    @Spy
    private TimeSlotMapper timeSlotMapper;
    @Mock
    private StreamObserver<AllTimeSlotsResponse> responseObserver;
    @Captor
    private ArgumentCaptor<Throwable> entityNotFoundCaptor;
    @InjectMocks
    private AdminCalendarServiceServer service;

    @Test
    public void getAllTimeSlots_success() {
        GrpcPageRequest grpcRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(
                TestConstantHolder.pageNumber,
                TestConstantHolder.pageSize
        );
        Page<MentorTimeSlotEntity> page = new PageImpl<>(
                List.of(TestEntityStubGenerator.constructMentorTimeSlotEntity()));

        AllTimeSlotsResponse expectedResponse = TestGrpcStubGenerator.constructAllTimeSlotsResponse();

        Mockito.when(repository.findAll(pageRequest)).thenReturn(page);

        service.getAllTimeSlots(grpcRequest, responseObserver);

        Mockito.verify(responseObserver).onNext(expectedResponse);
        Mockito.verify(responseObserver).onCompleted();
    }

    @Test
    void getAllTimeSlots_notFound() {
        GrpcPageRequest grpcRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(
                TestConstantHolder.pageNumber,
                TestConstantHolder.pageSize
        );

        Mockito.when(repository.findAll(pageRequest)).thenThrow(new EntityNotFoundException(
                TestConstantHolder.notFoundExceptionText));

        service.getAllTimeSlots(grpcRequest, responseObserver);

        Mockito.verify(responseObserver).onError(entityNotFoundCaptor.capture());
        Mockito.verify(responseObserver, Mockito.never()).onNext(Mockito.any());

        Throwable entityNotFoundException = entityNotFoundCaptor.getValue();
        Assertions.assertThat(entityNotFoundException)
                  .isInstanceOf(StatusRuntimeException.class)
                  .hasMessageContaining(TestConstantHolder.notFoundExceptionText);
    }

}