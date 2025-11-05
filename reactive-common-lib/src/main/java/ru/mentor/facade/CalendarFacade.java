package ru.mentor.facade;

import reactor.core.publisher.Mono;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.GrpcPageRequest;

public interface CalendarFacade {

    Mono<AllTimeSlotsResponse> findAllTimeSlotsResponseByGrpcPageRequest(GrpcPageRequest pageRequest);

}
