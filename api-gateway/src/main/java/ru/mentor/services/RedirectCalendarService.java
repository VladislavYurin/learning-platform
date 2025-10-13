package ru.mentor.services;

import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.dto.MentorTimeSlotInfoForUserDto;
import java.util.List;

/**
 * Сервис редиректа запросов в микросервис calendar-service
 */
public interface RedirectCalendarService {

    /**
     * Отправляет запрос на создание слота ментором.
     *
     * @param createRequest {@link MentorTimeSlotCreateRequest}
     * @return {@link MentorTimeSlotDto}
     */
    MentorTimeSlotDto createTimeSlot(MentorTimeSlotCreateRequest createRequest);

    /**
     * Отправляет запрос на бронирование слота по ID.
     *
     * @param timeSlotId ID слота для бронирования
     * @return {@link MentorTimeSlotDto}
     */
    MentorTimeSlotDto bookTimeSlot(long timeSlotId);

    /**
     * Отправляет запрос для получения всех слотов ментора с информацией об участниках.
     */
    List<MentorSlotInfoDto> getMentorSlotsInfoForMentor();

    /**
     * Отправляет запрос для получения всех слотов ментора без информации об участниках и с признаком заполненности
     */
    List<MentorTimeSlotInfoForUserDto> getMentorSlotsInfoForUser(Long mentorId);

    /**
     * Отправляет запрос для отмены слота.
     */
    String cancelTimeSlot(long timeSlotId);
}
