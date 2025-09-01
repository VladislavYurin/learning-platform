package ru.mentor.services;

import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;

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

}
