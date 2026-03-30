package ru.mentor.services.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.common.PageDetails;
import ru.mentor.constant.MdcKeys;
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.AdminCalendarServiceGrpcClient;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.services.RedirectAdminCalendarService;
import ru.mentor.services.UserService;

/**
 * Редирект сервис управления слотами. Необходимы права администратора.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedirectAdminCalendarServiceImpl implements RedirectAdminCalendarService {

    private final UserService userService;

    private final AdminCalendarServiceGrpcClient calendarServiceGrpcClient;

    private final TimeSlotMapper timeSlotMapper;

    private final BaseMapper baseMapper;

    private final HeaderFactory headerFactory;

    /**
     * Возвращает все слоты ментора с постраничностью.
     *
     * @param pageNumber
     *         номер страницы
     * @param pageSize
     *         размер страницы
     *
     * @return объект {@link Page}, содержащий {@link MentorSlotInfoDto}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<MentorSlotInfoDto> getAllMentorTimeSlots(int pageNumber, int pageSize) {

        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Long currentUserId = userService.getCurrentUserId();
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] [pageNumber={}] [pageSize={}] Получен запрос на извлечение всех слотов.",
                currentUserId,
                pageNumber,
                pageSize
        );

        GrpcPageRequest pageRequest = baseMapper.constructGrpcPageRequest(
                header,
                pageNumber,
                pageSize
        );

        try {
            AllTimeSlotsResponse allTimeSlots = calendarServiceGrpcClient.getAllTimeSlots(pageRequest);

            log.debug(
                    "[userId={}] [pageNumber={}] [pageSize={}] Успешно получен ответ от calendar-service на извлечение всех слотов.",
                    currentUserId,
                    pageNumber,
                    pageSize
            );

            List<MentorSlotInfoDto> mentorSlotInfoDtoList =
                    timeSlotMapper.mapGrpcAllTimeSlotsResponseToMentorSlotInfoDtoList(allTimeSlots);

            PageDetails pageDetails = allTimeSlots.getPageDetails();

            return new PageImpl<>(
                    mentorSlotInfoDtoList,
                    baseMapper.mapGrpcPageDetailsToPageRequest(pageDetails),
                    pageDetails.getTotalElements()
            );
        } catch (Exception e) {
            log.error(
                    "[userId={}] [pageNumber={}] [pageSize={}] Ошибка при вызове calendar-service во время извлечения всех слотов.",
                    currentUserId,
                    pageNumber,
                    pageSize,
                    e
            );
            throw e;
        }
    }
}
