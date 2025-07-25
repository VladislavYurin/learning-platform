package ru.mentor.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
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
    CourseDto createCourse(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody InnerCreateCourseRequest dto);

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/course/{userId}/{courseId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> deleteCourse(
            @RequestHeader("RqUId") String rqUId,
            @PathVariable Long userId,
            @PathVariable Long courseId);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/course/{userId}/{courseId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    CourseDto getCourseById(
            @RequestHeader("RqUId") String rqUId,
            @PathVariable Long userId,
            @PathVariable Long courseId);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/course/{userId}/all/active",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<CourseDto> getAllActiveCourses(
            @RequestHeader("RqUId") String rqUId,
            @PathVariable Long userId);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/course/{userId}/all",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<CourseDto> getAllCourses(@RequestHeader("RqUId") String rqUId, @PathVariable Long userId);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/module/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ModuleDto createModule(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody InnerCreateModuleRequest request);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/module/import",
            produces = MediaType.MULTIPART_FORM_DATA_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ModuleDto importModuleFromMarkdown(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody InnerCreateModuleRequest request,
            @RequestPart("file") MultipartFile file);

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/module/{userId}/{courseId}/{moduleId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> deleteModule(
            @RequestHeader("RqUId") String rqUId,
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
            @RequestHeader("RqUId") String rqUId,
            @PathVariable Long userId,
            @PathVariable Long courseId,
            @PathVariable Long moduleId);

}
