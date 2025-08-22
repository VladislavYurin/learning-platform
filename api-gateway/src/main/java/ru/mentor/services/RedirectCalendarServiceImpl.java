package ru.mentor.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentor.calendar.BookTimeSlotRequest;
import ru.mentor.calendar.CreateTimeSlotRequest;
import ru.mentor.calendar.TimeSlotResponse;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.grpc.CalendarServiceGrpcClient;
import ru.mentor.mapper.TimeSlotMapper;
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

    /**
     * Отправляет запрос на создание слота ментором.
     *
     * @param createRequest {@link MentorTimeSlotCreateRequest}
     * @return {@link MentorTimeSlotDto}
     */
    @Override
    public MentorTimeSlotDto createTimeSlot(MentorTimeSlotCreateRequest createRequest) {

        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info("[ RqUId = {} ] Получен запрос на создание слота ментором [ ID = {} ].",
                rqUId,
                user.getId());

        CreateTimeSlotRequest createTimeSlotGrpcRequest =
                timeSlotMapper.requestCreateToGrpcDto(createRequest, rqUId, user);

        TimeSlotResponse timeSlotGrpcResponse = calendarServiceClient
                .createMentorTimeSlot(createTimeSlotGrpcRequest);

        return timeSlotMapper.grpcResponseToDto(timeSlotGrpcResponse);
    }

    /**
     * Отправляет запрос на бронирование слота по ID.
     *
     * @param timeSlotId ID слота для бронирования
     * @return {@link MentorTimeSlotDto}
     */
    @Override
    public MentorTimeSlotDto bookTimeSlot(long timeSlotId) {

        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info("[ RqUId = {} ] Получен запрос на бронирование слота учеником [ ID = {} ].",
                rqUId,
                user.getId());

        BookTimeSlotRequest bookTimeSlotRequest =
                timeSlotMapper.toGrpcBookTimeSlotRequest(rqUId, timeSlotId, user.getId());

        TimeSlotResponse timeSlotGrpcResponse = calendarServiceClient
                .bookTimeSlot(bookTimeSlotRequest);

        return timeSlotMapper.grpcResponseToDto(timeSlotGrpcResponse);
    }
}