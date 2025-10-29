package ru.mentor.facade.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.MentorSlotInfo;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.facade.CalendarFacade;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.mapper.UserMapper;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.repository.UserRepository;

/**
 * Фасад для работы со слотами для админов
 * Абстракция для работы со связанными таблицами в реактивных репозиториях и для маппинга.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarFacadeImpl implements CalendarFacade {

    private final BaseMapper baseMapper;

    private final MentorTimeSlotRepository timeSlotRepository;

    private final TimeSlotMapper timeSlotMapper;

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    /**
     * Возвращает gRPC-ответ со списком тайм-слотов на основе входного запроса
     * {@link GrpcPageRequest}
     * Добавляет к ним информацию о пагинации и общем количестве
     *
     * @param grpcPageRequest
     *         объект запроса страницы с параметрами пагинации и идентификатором запроса
     *
     * @return реактивная обёртка {@link Mono}, содержащая {@link AllTimeSlotsResponse}
     */
    @Override
    public Mono<AllTimeSlotsResponse> findAllTimeSlotsResponseByGrpcPageRequest(
            GrpcPageRequest
                    grpcPageRequest) {
        return Mono.just(grpcPageRequest)
                   .flatMap(request -> {
                       PageRequest pageRequest =
                               baseMapper.mapGrpcPageRequestToPageRequest(request);
                       Mono<List<MentorSlotInfo>> mentorSlotInfoListMono =
                               findMentorSlotInfoList(pageRequest, request.getRequestId());
                       return mentorSlotInfoListMono
                               .zipWith(
                                       timeSlotRepository.count(),
                                       (timeSlotsList, totalTimeSlots) -> new PageImpl<>(
                                               timeSlotsList,
                                               pageRequest,
                                               totalTimeSlots
                                       )
                               )
                               .map(timeSlotMapper::mapMentorTimeSlotEntityPageToAllTimeSlotsResponse);
                   });
    }

    /**
     * Возвращает список тайм-слотов для заданной страницы в виде {@link MentorSlotInfo}.
     *
     * @param pageRequest
     *         объект {@link PageRequest}, определяющий номер страницы и её размер
     * @param requestId
     *         идентификатор запроса, используемый для трассировки
     *
     * @return реактивная обёртка {@link Mono}, содержащая список {@link MentorSlotInfo}
     */
    private Mono<List<MentorSlotInfo>> findMentorSlotInfoList(
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

    /**
     * Преобразует сущность тайм-слота {@link MentorTimeSlotEntity} в объект {@link MentorSlotInfo}
     *
     * @param timeSlotEntity
     *         сущность тайм-слота из базы данных
     * @param requestId
     *         идентификатор запроса для трассировки
     *
     * @return реактивная обёртка {@link Mono}, содержащая объект {@link MentorSlotInfo}
     */
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
