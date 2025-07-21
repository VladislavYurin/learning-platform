package ru.mentor.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.mentor.config.CommonFeignConfig;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.InnerCreateCourseRequest;
import ru.mentor.dto.InnerCreateModuleRequest;
import ru.mentor.dto.ModuleDto;

@FeignClient(
        name = "courseClient",
        url = "${integration.course-service.url}",
        configuration = {CommonFeignConfig.class}
)
public interface CourseClient {

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/course/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    CourseDto createCourse(@RequestBody InnerCreateCourseRequest dto);

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/course/{userId}/{courseId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> deleteCourse(@PathVariable Long userId, @PathVariable Long courseId);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/course/{userId}/{courseId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    CourseDto getCourseById(@PathVariable Long userId, @PathVariable Long courseId);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/course/{userId}/all/active",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<CourseDto> getAllActiveCourses(@PathVariable Long userId);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/course/{userId}/all",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<CourseDto> getAllCourses(@PathVariable Long userId);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/module/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ModuleDto createModule(@RequestBody InnerCreateModuleRequest request);

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/module/{userId}/{courseId}/{moduleId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> deleteModule(
            @PathVariable Long userId,
            @PathVariable Long courseId,
            @PathVariable Long moduleId);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/module/{userId}/{courseId}/{moduleId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ModuleDto getModuleById(
            @PathVariable Long userId,
            @PathVariable Long courseId,
            @PathVariable Long moduleId);

}
