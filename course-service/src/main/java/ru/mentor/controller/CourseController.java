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
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.InnerCreateCourseRequest;
import ru.mentor.service.CourseService;

/**
 * Контроллер для работы с курсами.
 * Контроллер для обработки запросов, связанных с курсами.
 * Этот контроллер предоставляет API для создания, удаления и получения информации о курсах.
 */
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Tag(name = "Работа с курсами")
public class CourseController {

    private final CourseService courseService;

    /**
     * Создает новый курс на основе предоставленного запроса.
     *
     * @param request Запрос, содержащий данные для создания курса.
     * @return ResponseEntity с DTO курса, который был создан.
     */
    @PostMapping("/create")
    public ResponseEntity<CourseDto> createCourse(@RequestBody @Valid InnerCreateCourseRequest request) {
        return ResponseEntity.ok().body(courseService.createCourse(request));
    }

    /**
     * Удаляет курс по идентификатору.
     *
     * @param userId Идентификатор пользователя, инициирующего удаление курса.
     * @param courseId Идентификатор курса, который необходимо удалить.
     * @return ResponseEntity с пустым телом и статусом 200 OK.
     */
    @DeleteMapping("/{userId}/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long userId, @PathVariable Long courseId) {
        courseService.deleteCourse(userId, courseId);
        return ResponseEntity.ok().body(null);
    }

    /**
     * Получает курс по его идентификатору.
     *
     * @param userId Идентификатор пользователя, запрашивающего курс.
     * @param courseId Идентификатор курса, который нужно получить.
     * @return ResponseEntity с DTO курса, найденного по идентификатору.
     */
    @GetMapping("/{userId}/{courseId}")
    public ResponseEntity<CourseDto> getCourseById(
            @PathVariable Long userId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok().body(courseService.getCourseById(userId, courseId));
    }

    /**
     * Получает список всех активных курсов для указанного пользователя.
     *
     * @param userId Идентификатор пользователя, для которого требуется получить активные курсы.
     * @return ResponseEntity со списком активных курсов в виде DTO.
     */
    @GetMapping("/{userId}/all/active")
    public ResponseEntity<List<CourseDto>> getAllActiveCourses(@PathVariable Long userId) {
        return ResponseEntity.ok().body(courseService.getAllActiveCourses(userId));
    }

    /**
     * Получает список всех курсов для указанного пользователя.
     *
     * @param userId Идентификатор пользователя, для которого требуется получить курсы.
     * @return ResponseEntity со списком курсов в виде DTO.
     */
    @GetMapping("/{userId}/all")
    public ResponseEntity<List<CourseDto>> getAllCourses(@PathVariable Long userId) {
        return ResponseEntity.ok().body(courseService.getAllCourses(userId));
    }

}
