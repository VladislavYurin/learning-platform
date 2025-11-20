package ru.mentor.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.gateway.api.AdminMentorTimeSlotControllerApi;
import ru.mentor.gateway.model.PageMentorSlotInfoDto;
import ru.mentor.mapper.PageMentorSlotInfoDtoMapper;
import ru.mentor.services.RedirectAdminCalendarService;

/**
 * Контроллер для управления слотами для администратора.
 */
@RestController
@RequiredArgsConstructor
public class AdminMentorTimeSlotController implements AdminMentorTimeSlotControllerApi {

    private final RedirectAdminCalendarService redirectCalendarService;
    private final PageMentorSlotInfoDtoMapper pageMentorSlotInfoDtoMapper;

    /**
     * Реализация ручки GET /admin/slot/all
     */
    @Override
    public ResponseEntity<PageMentorSlotInfoDto> adminGetMentorSlotsInfo(Integer pageNumber, Integer pageSize) {
        Page<MentorSlotInfoDto> page = redirectCalendarService.getAllMentorTimeSlots(pageNumber, pageSize);
        return ResponseEntity.ok(pageMentorSlotInfoDtoMapper.toApiDto(page));
    }
}
