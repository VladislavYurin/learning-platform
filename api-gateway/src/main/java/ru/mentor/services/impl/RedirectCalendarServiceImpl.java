package ru.mentor.services.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentor.common.BookTimeSlotRequest;
import ru.mentor.common.CancelTimeSlotRequest;
import ru.mentor.common.CancelTimeSlotResponse;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.Header;
import ru.mentor.common.MentorSlotInfo;
import ru.mentor.common.MentorSlotsInfoRequest;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.dto.MentorTimeSlotInfoForUserDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.CalendarServiceGrpcClient;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.services.RedirectCalendarService;
import ru.mentor.services.UserService;
import ru.mentor.util.RqGenerator;

/**
 * Сервис редиректа запросов в микросервис calendar-service
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RedirectCalendarServiceImpl implements RedirectCalendarService {

    private final UserService userService;

    private final CalendarServiceGrpcClient calendarServiceClient;

    private final TimeSlotMapper timeSlotMapper;

    private final HeaderFactory headerFactory;

    /**
     * Отправляет запрос на создание слота ментором.
     *
     * @param createRequest
     *         {@link MentorTimeSlotCreateRequest}
     *
     * @return {@link MentorTimeSlotDto}
     */
    @Override
    public MentorTimeSlotDto createTimeSlot(MentorTimeSlotCreateRequest createRequest) {

        UserEntity user = userService.getCurrentUser();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ requestId = {} ] Получен запрос на создание слота ментором [ ID = {} ].",
                requestId,
                user.getId()
        );

        CreateTimeSlotRequest createTimeSlotGrpcRequest =
                timeSlotMapper.toCreateTimeSlotRequest(createRequest, header, user);

        TimeSlotResponse timeSlotGrpcResponse = calendarServiceClient
                .createMentorTimeSlot(createTimeSlotGrpcRequest);

        return timeSlotMapper.timeSlotResponseToMentorTimeSlotDto(timeSlotGrpcResponse);
    }

    /**
     * Отправляет запрос на бронирование слота по ID.
     *
     * @param timeSlotId
     *         ID слота для бронирования
     *
     * @return {@link MentorTimeSlotDto}
     */
    @Override
    public MentorTimeSlotDto bookTimeSlot(long timeSlotId) {

        UserEntity user = userService.getCurrentUser();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ requestId = {} ] Получен запрос на бронирование слота [ ID = {}] учеником [ ID = {} ].",
                requestId,
                timeSlotId,
                user.getId()
        );

        BookTimeSlotRequest bookTimeSlotRequest = timeSlotMapper
                .toBookTimeSlotRequest(header, timeSlotId, user.getId());

        TimeSlotResponse timeSlotGrpcResponse = calendarServiceClient
                .bookTimeSlot(bookTimeSlotRequest);

        return timeSlotMapper.timeSlotResponseToMentorTimeSlotDto(timeSlotGrpcResponse);
    }

    public String cancelTimeSlot(long timeSlotId) {

        Long userId = userService.getCurrentUser().getId();
        String rqUId = RqGenerator.generateRqId();
        Header header = headerFactory.create(rqUId);
        log.info("[ RqUId = {} ] Получен запрос на отмену бронирования слота [ ID = {}] учеником [ ID = {} ].",
                rqUId,
                timeSlotId,
                userId);

        CancelTimeSlotRequest cancelTimeSlotRequest =
                timeSlotMapper.toGrpcCancelTimeSlotRequest(header, timeSlotId, userId);

        CancelTimeSlotResponse cancelTimeSlotResponse = calendarServiceClient.cancelTimeSlot(cancelTimeSlotRequest);
        return timeSlotMapper.grpcCancelTimeSlotResponseToDto(cancelTimeSlotResponse);
    }

    /**
     * Отправляет запрос для получения информации о слотах текущего ментора и участниках в этих
     * слотах
     *
     * @return список ДТО {@link List<MentorSlotInfoDto>}
     */
    @Override
    public List<MentorSlotInfoDto> getMentorSlotsInfoForMentor() {

        return timeSlotMapper.toSlotInfoDtoList(
                this.getMentorSlotsInfoRequest(Optional.empty()));
    }

    /**
     * Отправляет запрос для получения информации о слотах ментора с ID = mentorId
     *
     * @param mentorId
     *         - ID ментора, слоты которого нужно вернуть
     *
     * @return список ДТО {@link List<MentorTimeSlotInfoForUserDto>}
     */
    @Override
    public List<MentorTimeSlotInfoForUserDto> getMentorSlotsInfoForUser(Long mentorId) {

        return timeSlotMapper.mentorSlotInfoListToMentorTimeSlotInfoForUserDtoList(
                this.getMentorSlotsInfoRequest(Optional.of(mentorId)));
    }

    public List<MentorSlotInfo> getMentorSlotsInfoRequest(Optional<Long> mentorId) {

        Long userId = userService.getCurrentUser().getId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);

        log.info(
                "[ requestId = {} ] Получен запрос от пользователя [ ID = {} ] на извлечение информации о слотах ментора [ ID = {} ].",
                requestId,
                userId,
                mentorId.orElse(userId)
        );

        MentorSlotsInfoRequest request =
                timeSlotMapper.toMentorSlotsInfoGrpcRequest(mentorId.orElse(userId), header);

        return calendarServiceClient.getMentorSlotsInfo(request).getSlotsList();
    }

}