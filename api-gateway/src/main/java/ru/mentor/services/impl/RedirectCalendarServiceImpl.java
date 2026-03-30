package ru.mentor.services.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import ru.mentor.common.BookTimeSlotRequest;
import ru.mentor.common.CancelTimeSlotRequest;
import ru.mentor.common.CancelTimeSlotResponse;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.Header;
import ru.mentor.common.MentorSlotInfo;
import ru.mentor.common.MentorSlotsInfoRequest;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.constant.MdcKeys;
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
        Long userId = user.getId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] Получен запрос на создание слота ментором.",
                userId
        );

        CreateTimeSlotRequest createTimeSlotGrpcRequest =
                timeSlotMapper.requestCreateToGrpcDto(createRequest, header, user);

        try {
            TimeSlotResponse timeSlotGrpcResponse =
                    calendarServiceClient.createMentorTimeSlot(createTimeSlotGrpcRequest);

            log.debug(
                    "[userId={}] Успешно получен ответ от calendar-service на создание слота.",
                    userId
            );

            return timeSlotMapper.grpcResponseToDto(timeSlotGrpcResponse);
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при вызове calendar-service во время создания слота.",
                    userId,
                    e
            );
            throw e;
        }
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
        Long userId = user.getId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] Получен запрос на бронирование слота [timeSlotId={}].",
                userId,
                timeSlotId
        );

        BookTimeSlotRequest bookTimeSlotRequest = timeSlotMapper
                .toGrpcBookTimeSlotRequest(header, timeSlotId, userId);

        try {
            TimeSlotResponse timeSlotGrpcResponse =
                    calendarServiceClient.bookTimeSlot(bookTimeSlotRequest);

            log.debug(
                    "[userId={}] Успешно получен ответ от calendar-service на бронирование слота [timeSlotId={}].",
                    userId,
                    timeSlotId
            );

            return timeSlotMapper.grpcResponseToDto(timeSlotGrpcResponse);
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при вызове calendar-service во время бронирования слота [timeSlotId={}].",
                    userId,
                    timeSlotId,
                    e
            );
            throw e;
        }
    }

    @Override
    public String cancelTimeSlot(long timeSlotId) {

        Long userId = userService.getCurrentUser().getId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] Получен запрос на отмену бронирования слота [timeSlotId={}].",
                userId,
                timeSlotId
        );

        CancelTimeSlotRequest cancelTimeSlotRequest =
                timeSlotMapper.toGrpcCancelTimeSlotRequest(header, timeSlotId, userId);

        try {
            CancelTimeSlotResponse cancelTimeSlotResponse =
                    calendarServiceClient.cancelTimeSlot(cancelTimeSlotRequest);

            log.debug(
                    "[userId={}] Успешно получен ответ от calendar-service на отмену бронирования слота [timeSlotId={}].",
                    userId,
                    timeSlotId
            );

            return timeSlotMapper.grpcCancelTimeSlotResponseToDto(cancelTimeSlotResponse);
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при вызове calendar-service во время отмены бронирования слота [timeSlotId={}].",
                    userId,
                    timeSlotId,
                    e
            );
            throw e;
        }
    }

    /**
     * Отправляет запрос для получения информации о слотах текущего ментора и участниках в этих слотах
     *
     * @return список ДТО {@link List<MentorSlotInfoDto>}
     */
    @Override
    public List<MentorSlotInfoDto> getMentorSlotsInfoForMentor() {
        return timeSlotMapper.toSlotInfoDtoList(
                this.getMentorSlotsInfoRequest(Optional.empty())
        );
    }

    /**
     * Отправляет запрос для получения информации о слотах ментора с ID = mentorId
     *
     * @param mentorId
     *         ID ментора, слоты которого нужно вернуть
     *
     * @return список ДТО {@link List<MentorTimeSlotInfoForUserDto>}
     */
    @Override
    public List<MentorTimeSlotInfoForUserDto> getMentorSlotsInfoForUser(Long mentorId) {
        return timeSlotMapper.toSlotInfoForUserList(
                this.getMentorSlotsInfoRequest(Optional.of(mentorId))
        );
    }

    public List<MentorSlotInfo> getMentorSlotsInfoRequest(Optional<Long> mentorId) {

        Long userId = userService.getCurrentUser().getId();
        Long mentorIdValue = mentorId.orElse(userId);
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] Получен запрос на извлечение информации о слотах ментора [mentorId={}].",
                userId,
                mentorIdValue
        );

        MentorSlotsInfoRequest request =
                timeSlotMapper.toMentorSlotsInfoGrpcRequest(mentorIdValue, header);

        try {
            List<MentorSlotInfo> slots = calendarServiceClient.getMentorSlotsInfo(request).getSlotsList();

            log.debug(
                    "[userId={}] Успешно получен ответ от calendar-service на извлечение информации о слотах ментора [mentorId={}].",
                    userId,
                    mentorIdValue
            );

            return slots;
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при вызове calendar-service во время извлечения информации о слотах ментора [mentorId={}].",
                    userId,
                    mentorIdValue,
                    e
            );
            throw e;
        }
    }
}