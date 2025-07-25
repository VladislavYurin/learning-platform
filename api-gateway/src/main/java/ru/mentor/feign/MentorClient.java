package ru.mentor.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.mentor.config.CommonFeignConfig;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.GetAccessRequest;

@FeignClient(
        name = "mentorClient",
        url = "${integration.access-service.url}",
        configuration = {CommonFeignConfig.class}
)
public interface MentorClient {

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/access/course/get-access",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> getCourseAccessToUser(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody GetAccessRequest dto);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/course/delete-access",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> deleteCourseAccessToUser(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody GetAccessRequest dto);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/module/get-access",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> getModuleAccessToUser(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody GetAccessRequest dto);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/module/delete-access",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> deleteModuleAccessToUser(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody GetAccessRequest dto);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/progress/course/{mentorId}/{courseId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<CourseProgressResponse> getCourseProgressByMentor(
            @RequestHeader("RqUId") String rqUId,
            @PathVariable Long mentorId,
            @PathVariable Long courseId);

}
