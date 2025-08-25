package ru.mentor.services.impl;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.mentor.admin.AllTimeSlotsResponse;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.PageDetails;
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.dto.PageSettings;
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

    /**
     * Возвращает все слоты ментора.
     *
     * @param pageSettings
     *         Настройки страницы (номер страницы и размер)
     *
     * @return объект {@link Page}, содержащий {@link MentorSlotInfoDto}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<MentorSlotInfoDto> getAllMentorTimeSlots(PageSettings pageSettings) {

        String requestId = UUID.randomUUID().toString();
        Long currentUserId = userService.getCurrentUserId();

        log.info(
                "Получен запрос [ requestId = {} ] от администратора [ ID = {} ] на извлечение всех слотов",
                requestId,
                currentUserId
        );

        GrpcPageRequest pageRequest = baseMapper.constructGrpcPageRequest(requestId, pageSettings);
        AllTimeSlotsResponse allTimeSlots = calendarServiceGrpcClient.getAllTimeSlots(pageRequest);

        List<MentorSlotInfoDto> mentorSlotInfoDtoList =
                timeSlotMapper.mapGrpcAllTimeSlotsResponseToMentorSlotInfoDtoList(allTimeSlots);

        PageDetails pageDetails = allTimeSlots.getPageDetails();
        return new PageImpl<>(
                mentorSlotInfoDtoList,
                baseMapper.mapGrpcPageDetailsToPageRequest(pageDetails),
                pageDetails.getTotalElements()
        );
    }

}
