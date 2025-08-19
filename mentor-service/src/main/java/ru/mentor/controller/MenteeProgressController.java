package ru.mentor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.MenteeProgressDto;
import ru.mentor.service.ProgressService;

/**
 * Контроллер для получения информации о прогрессе обучения менторов и учеников.
 *
 * Данный контроллер предоставляет API для получения статистики по прогрессу
 * учеников в курсах и списка всех учеников, которые принимают участие
 * в указанном курсе под руководством заданного наставника.
 */
@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
@Tag(name = "Получение процесса обучения менти")
@Slf4j
public class MenteeProgressController {

    private final ProgressService progressService;

    /**
     * Получает статистику прогресса учеников в курсе для заданного наставника.
     *
     * @param rqUId Идентификатор запроса, ассоциированный с текущей сессией.
     * @param mentorId Идентификатор наставника, чьи ученики должны быть проверены.
     * @param courseId Идентификатор курса, для которого запрашивается статистика.
     * @return Ответ с объектом CourseProgressResponse, содержащим статистику прогресса.
     */
    @GetMapping("/course/{mentorId}/{courseId}/statistics")
    public ResponseEntity<CourseProgressResponse> getCourseProgressByMentor(
            @RequestHeader("RqUId") String rqUId,
            @PathVariable Long mentorId,
            @PathVariable Long courseId) {
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на получение прогресса учеников в курсе [ ID = %d ] ментором [ ID = %d ].",
                rqUId,
                courseId,
                mentorId
        ));
        CourseProgressResponse response = progressService.getCourseProgressByMentor(
                mentorId,
                courseId
        );
        log.info(String.format(
                "[ RqUId = %s ] Успешно обработан запрос на получение прогресса учеников в курсе [ ID = %d ] ментором [ ID = %d ].",
                rqUId,
                courseId,
                mentorId
        ));
        return ResponseEntity.ok().body(response);
    }

    /**
     * Получает список всех учеников, участвующих в курсе под руководством заданного наставника.
     *
     * @param rqUId Идентификатор запроса, ассоциированный с текущей сессией.
     * @param mentorId Идентификатор наставника, чьи ученики должны быть проверены.
     * @param courseId Идентификатор курса, для которого запрашивается список учеников.
     * @return Ответ с списком объектов MenteeProgressDto, представляющих учеников.
     */
    @GetMapping("/course/{mentorId}/{courseId}/users")
    public ResponseEntity<List<MenteeProgressDto>> getAllUsersAtCourse(
            @RequestHeader("RqUId") String rqUId,
            @PathVariable Long mentorId,
            @PathVariable Long courseId) {
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на получение всех учеников в курсе [ ID = %d ] ментором [ ID = %d ].",
                rqUId,
                courseId,
                mentorId
        ));
        List<MenteeProgressDto> response = progressService.getAllUsersAtCourse(
                mentorId,
                courseId
        );
        log.info(String.format(
                "[ RqUId = %s ] Успешно обработан запрос на получение всех учеников в курсе [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                courseId,
                mentorId
        ));
        return ResponseEntity.ok().body(response);
    }

}
