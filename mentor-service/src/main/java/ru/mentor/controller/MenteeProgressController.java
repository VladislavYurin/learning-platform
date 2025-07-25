package ru.mentor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.MenteeProgressDto;
import ru.mentor.service.ProgressService;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
@Tag(name = "Получение процесса обучения менти")
public class MenteeProgressController {

    private final ProgressService progressService;

    @GetMapping("/course/{mentorId}/{courseId}")
    public ResponseEntity<CourseProgressResponse> getCourseProgressByMentor(
            @PathVariable Long mentorId,
            @PathVariable Long courseId) {
        CourseProgressResponse response = progressService.getCourseProgressByMentor(
                mentorId,
                courseId
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/course/{mentorId}/{courseId}/users")
    public ResponseEntity<List<MenteeProgressDto>> getAllUsersAtCourse(
            @PathVariable Long mentorId,
            @PathVariable Long courseId) {
        List<MenteeProgressDto> response = progressService.getAllUsersAtCourse(
                mentorId,
                courseId
        );
        return ResponseEntity.ok().body(response);
    }

}
