package ru.mentor.grpc;

import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;
import ru.mentor.admin.ReactorAdminCalendarServiceGrpc;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.facade.CalendarFacade;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.util.GrpcRequestLogContext;

/**
 * gRPC-сервис для работы со слотами для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AdminCalendarServiceServer extends
        ReactorAdminCalendarServiceGrpc.AdminCalendarServiceImplBase {

    private final CalendarFacade calendarFacade;

    /**
     * Возвращает gRPC-объект со списком слотов внутри реактивной обертки {@link Mono}
     *
     * @param requestMono
     *         gRPC-объект {@link GrpcPageRequest} запроса страницы внутри реактивной обертки
     *         {@link Mono}
     *
     * @return gRPC-объект со списком слотов внутри реактивной обертки {@link Mono}
     */
    @Override
    public Mono<AllTimeSlotsResponse> getAllTimeSlots(Mono<GrpcPageRequest> requestMono) {
        return requestMono
                .switchIfEmpty(Mono.error(Status.INVALID_ARGUMENT
                        .withDescription(GrpcErrorText.EMPTY_REQUEST)
                        .asRuntimeException()))
                .flatMap(grpcPageRequest -> {
                    String requestId = grpcPageRequest.getHeader().getRequestId();

                    GrpcRequestLogContext.withRequestId(requestId, () ->
                            log.debug(
                                    "Получен gRPC запрос на получение страницы слотов: [pageNumber={}] [pageSize={}] [senderId={}]",
                                    grpcPageRequest.getPageNumber(),
                                    grpcPageRequest.getPageSize(),
                                    grpcPageRequest.getSenderId()
                            )
                    );

                    return calendarFacade.findAllTimeSlotsResponseByGrpcPageRequest(grpcPageRequest)
                            .doOnSuccess(response ->
                                    GrpcRequestLogContext.withRequestId(requestId, () ->
                                            log.debug(
                                                    "Успешно обработан gRPC запрос на получение страницы слотов: [pageNumber={}] [pageSize={}] [senderId={}]",
                                                    grpcPageRequest.getPageNumber(),
                                                    grpcPageRequest.getPageSize(),
                                                    grpcPageRequest.getSenderId()
                                            )
                                    )
                            )
                            .doOnError(error ->
                                    GrpcRequestLogContext.withRequestId(requestId, () ->
                                            log.error(
                                                    "Ошибка обработки gRPC запроса на получение страницы слотов: [pageNumber={}] [pageSize={}] [senderId={}] [cause={}]",
                                                    grpcPageRequest.getPageNumber(),
                                                    grpcPageRequest.getPageSize(),
                                                    grpcPageRequest.getSenderId(),
                                                    GrpcRequestLogContext.buildErrorDescription(error),
                                                    error
                                            )
                                    )
                            );
                })
                .onErrorMap(
                        EntityNotFoundException.class,
                        e -> Status.NOT_FOUND
                                .withDescription(e.getMessage())
                                .asRuntimeException()
                );
    }
}