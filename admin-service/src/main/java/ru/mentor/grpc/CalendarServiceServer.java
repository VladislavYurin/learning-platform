package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.mentor.admin.AdminCalendarServiceGrpc.AdminCalendarServiceImplBase;
import ru.mentor.admin.AllTimeSlotsResponse;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.repository.MentorTimeSlotRepository;

/**
 * gRPC-сервис для работы со слотами для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CalendarServiceServer extends AdminCalendarServiceImplBase {

    private final BaseMapper baseMapper;

    private final MentorTimeSlotRepository timeSlotRepository;

    private final TimeSlotMapper timeSlotMapper;

    /**
     * Возвращает gRPC-объект со списком слотов
     *
     * @param request
     *         gRPC-объект {@link GrpcPageRequest} запроса страницы
     * @param responseObserver
     *         объект для возврата ответа
     */
    @Override
    public void getAllTimeSlots(
            GrpcPageRequest request,
            StreamObserver<AllTimeSlotsResponse> responseObserver) {

        String requestId = request.getRequestId();
        log.info(
                "Поступил запрос [ ID = {} ] на получение страницы слотов с номером [{}] размером [{}] от администратора",
                requestId,
                request.getPageNumber(),
                request.getPageSize()
        );

        try {
            PageRequest pageRequest = baseMapper.mapGrpcPageRequestToPageRequest(request);
            Page<MentorTimeSlotEntity> courseEntityPage = timeSlotRepository.findAll(pageRequest);

            AllTimeSlotsResponse allCoursesResponse =
                    timeSlotMapper
                            .mapMentorTimeSlotEntityPageToAllTimeSlotsResponse(
                                    courseEntityPage,
                                    requestId
                            );

            responseObserver.onNext(allCoursesResponse);
            responseObserver.onCompleted();

        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        }
    }

}
