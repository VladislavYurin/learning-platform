package ru.mentor.grpc;

import io.grpc.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import ru.mentor.admin.AllTimeSlotsResponse;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.ReactorAdminCalendarServiceGrpc;
import ru.mentor.calendar.MentorSlotInfo;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.mapper.UserMapper;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.repository.UserRepository;

/**
 * gRPC-сервис для работы со слотами для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AdminCalendarServiceServer extends
        ReactorAdminCalendarServiceGrpc.AdminCalendarServiceImplBase {

    private final MentorTimeSlotRepository timeSlotRepository;

    private final UserRepository userRepository;

    private final BaseMapper baseMapper;

    private final UserMapper userMapper;

    private final TimeSlotMapper timeSlotMapper;

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
                       .doOnNext(
                               request -> log.info(
                                       "[RqUid={}] Поступил запрос на получение страницы слотов: pageNumber={}, pageSize={}",
                                       request.getRequestId(),
                                       request.getPageNumber(),
                                       request.getPageSize()
                               ))
                       .flatMap(request -> {

                           PageRequest pageRequest =
                                   baseMapper.mapGrpcPageRequestToPageRequest(request);

                           Mono<List<MentorSlotInfo>> mentorSlotInfoListMono =
                                   getMentorSlotInfoList(pageRequest, request.getRequestId());

                           return mentorSlotInfoListMono
                                          .zipWith(
                                                  timeSlotRepository.count(),
                                                  (timeSlotsList, totalTimeSlots) -> new PageImpl<>(
                                                          timeSlotsList,
                                                          pageRequest,
                                                          totalTimeSlots
                                                  )
                                          )
                                          .map(timeSlotMapper::mapMentorTimeSlotEntityPageToAllTimeSlotsResponse)
                                          .onErrorMap(
                                                  EntityNotFoundException.class,
                                                  e -> Status.NOT_FOUND
                                                               .withDescription(e.getMessage())
                                                               .asRuntimeException()
                                          );
                       });
    }

    private Mono<List<MentorSlotInfo>> getMentorSlotInfoList(
            PageRequest pageRequest,
            String requestId) {

        return timeSlotRepository
                       .findAllBy(pageRequest)
                       .flatMap(timeSlotEntity -> toMentorSlotInfo(
                               timeSlotEntity,
                               requestId
                       ))
                       .collectList();
    }

    private Mono<MentorSlotInfo> toMentorSlotInfo(
            MentorTimeSlotEntity timeSlotEntity,
            String requestId) {

        return Mono.zip(
                Mono.just(timeSlotMapper.entityToGrpcResponse(timeSlotEntity, requestId)),
                userRepository.findAllSlotParticipantsBySlotId(timeSlotEntity.getId())
                              .map(userMapper::mapUserEntityToUserInfo)
                              .collectList(),
                timeSlotMapper::mentorTimeSlotEntityToMentorSlotInfo
        );
    }

}
