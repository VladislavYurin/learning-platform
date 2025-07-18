package ru.mentor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.Course;
import ru.mentor.dto.InnerCreateCourseRequest;
import ru.mentor.service.CourseService;

/**
 * Контроллер для работы с курсами.
 */
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Tag(name = "Работа с курсами")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/create")
    public ResponseEntity<Course> createCourse(@RequestBody @Valid InnerCreateCourseRequest request) {
        return ResponseEntity.ok().body(courseService.createCourse(request));
    }

    @DeleteMapping("/delete/{userId}/{courseId}}")
    public ResponseEntity<?> delete(@PathVariable Long userId, @PathVariable Long courseId) {
        courseService.deleteCourse(userId, courseId);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok().body(courseService.getCourseById(courseId));
    }

    @GetMapping("/all/active")
    public ResponseEntity<List<Course>> getAllActiveCourses() {
        return ResponseEntity.ok().body(courseService.getAllActiveCourses());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok().body(courseService.getAllCourses());
    }

}
