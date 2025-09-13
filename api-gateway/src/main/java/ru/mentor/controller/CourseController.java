package ru.mentor.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.front.CreateCourseRequest;
import ru.mentor.services.RedirectCourseService;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final RedirectCourseService redirectCourseService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<CourseDto> createCourse(@RequestBody CreateCourseRequest request) {
        return ResponseEntity.ok().body(redirectCourseService.createCourse(request));
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        redirectCourseService.deleteCourse(courseId);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok().body(redirectCourseService.getCourseById(courseId));
    }

    @GetMapping("/all/active")
    public ResponseEntity<List<CourseDto>> getAllActiveCourses() {
        return ResponseEntity.ok().body(redirectCourseService.getAllActiveCourses());
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok().body(redirectCourseService.getAllCourses());
    }

}
