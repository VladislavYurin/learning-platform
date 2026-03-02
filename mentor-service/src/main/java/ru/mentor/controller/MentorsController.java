package ru.mentor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.service.MentorService;

import java.util.List;

/**
 * Контроллер для работы с менторами
 * Предоставляет REST-эндпоинт для получения списка всех менторов
 */

@RestController
@RequestMapping("/mentors")
@RequiredArgsConstructor
@Tag(name = "Список менторов")
@Slf4j
public class MentorsController {

    private final MentorService mentorService;

    /**
     * Возвращает список всех менторов.
     * @param requestId идентификатор запроса, используемый для трассировки логов
     * @return HTTP-ответ со списком менторов
     */
    @GetMapping
    public ResponseEntity<List<UserInfoDto>> getAllMentors(
            @RequestHeader("requestId") String requestId) {
        log.info("[ requestId = {} ] Запрос на получение списка всех менторов.", requestId);
        List<UserInfoDto> response = mentorService.getAllMentors();
        log.info("[ requestId = {} ] Успешно возвращён список менторов, размер = {}", requestId, response.size());
        return ResponseEntity.ok().body(response);
    }
}
