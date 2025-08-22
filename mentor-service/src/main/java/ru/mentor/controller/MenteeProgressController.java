package ru.mentor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.MenteeProgressDto;
import ru.mentor.service.ProgressService;

import java.util.List;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
@Tag(name = "Получение процесса обучения менти")
@Slf4j
public class MenteeProgressController {

    private final ProgressService progressService;

    @GetMapping("/course/{mentorId}/{courseId}/statistics")
    @PreAuthorize("hasRole('MENTOR')")
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

    @GetMapping("/course/{mentorId}/{courseId}/users")
    @PreAuthorize("hasRole('MENTOR')")
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
