package ru.mentor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.services.RedirectProgressService;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
@Tag(name = "Получение процесса обучения менти")
public class ProgressController {

    private final RedirectProgressService redirectProgressService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseProgressResponse> getCourseProgressByMentor(@PathVariable Long courseId) {
        CourseProgressResponse response = redirectProgressService.getCourseProgressByMentor(courseId);
        return ResponseEntity.ok().body(response);
    }

}
