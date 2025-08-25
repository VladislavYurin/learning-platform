package ru.mentor.services;

import org.springframework.data.domain.Page;
import ru.mentor.dto.MentorSlotInfoDto;

/**
 * Редирект сервис управления слотами. Необходимы права администратора.
 */
public interface RedirectAdminCalendarService {

    /**
     * Возвращает все слоты ментора с постраничностью.
     *
     * @param pageNumber
     *         номер страницы
     *
     * @param pageSize
     *         размер страницы
     *
     * @return объект {@link Page}, содержащий {@link MentorSlotInfoDto}
     */
    Page<MentorSlotInfoDto> getAllMentorTimeSlots(int pageNumber, int pageSize);

}
