package ru.mentor.grpc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

@ExtendWith(MockitoExtension.class)
class CalendarServiceServerTest {

    @Mock
    private BaseMapper baseMapper;
    @Mock
    private MentorTimeSlotRepository repository;
    @Mock
    private TimeSlotMapper timeSlotMapper;
    @Mock
    private StreamObserver<AllTimeSlotsResponse> responseObserver;

    @InjectMocks
    private CalendarServiceServer service;

    private final String requestId = UUID.randomUUID().toString();
    private final int pageNumber = 0;
    private final int pageSize = 10;

    private final String notFoundExceptionText = "not found";

    private GrpcPageRequest constructGrpcPageRequest() {
        return GrpcPageRequest.newBuilder()
                              .setRequestId(requestId)
                              .setPageNumber(pageNumber)
                              .setPageSize(pageSize)
                              .build();
    }

    @Test
    void getAllTimeSlots_success() {
        // given
        GrpcPageRequest grpcRequest = constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<MentorTimeSlotEntity> page = new PageImpl<>(
                List.of(new MentorTimeSlotEntity()));

        AllTimeSlotsResponse expectedResponse = AllTimeSlotsResponse.newBuilder().build();

        Mockito.when(baseMapper.mapGrpcPageRequestToPageRequest(grpcRequest))
               .thenReturn(pageRequest);
        Mockito.when(repository.findAll(pageRequest)).thenReturn(page);
        Mockito.when(timeSlotMapper.mapMentorTimeSlotEntityPageToAllTimeSlotsResponse(
                       page,
                       requestId
               ))
               .thenReturn(expectedResponse);

        // when
        service.getAllTimeSlots(grpcRequest, responseObserver);

        // then
        Mockito.verify(responseObserver).onNext(expectedResponse);
        Mockito.verify(responseObserver).onCompleted();
        Mockito.verify(responseObserver, Mockito.never()).onError(Mockito.any());
    }

    @Test
    void getAllTimeSlots_notFound() {
        // given
        GrpcPageRequest grpcRequest = constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Mockito.when(baseMapper.mapGrpcPageRequestToPageRequest(grpcRequest))
               .thenReturn(pageRequest);
        Mockito.when(repository.findAll(pageRequest)).thenThrow(new EntityNotFoundException(
                notFoundExceptionText));

        // when
        service.getAllTimeSlots(grpcRequest, responseObserver);

        // then
        Mockito.verify(responseObserver).onError(argThat(error -> {
            assertThatThrownBy(() -> {throw error;})
                    .isInstanceOf(StatusRuntimeException.class)
                    .hasMessageContaining(notFoundExceptionText);
            return true;
        }));
        Mockito.verify(responseObserver, Mockito.never()).onNext(Mockito.any());
        Mockito.verify(responseObserver, Mockito.never()).onCompleted();
    }

}