package ru.mentor.services;

import org.springframework.data.domain.Page;
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.dto.PageSettings;

public interface RedirectAdminCalendarService {

    public Page<MentorSlotInfoDto> getAllMentorTimeSlots(PageSettings pageSettings);

}
