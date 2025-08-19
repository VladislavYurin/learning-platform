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
import ru.mentor.config.CommonFeignConfig;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.dto.MenteeProgressDto;

/**
 * Клиент OpenFeign для управления доступом наставника во внешнем сервисе.
 * <p>
 *     Инкапсулирует вызовы API управления доступом. Для трассировки
 *     каждый запрос передаёт корелляционный заголовок {@code RqUId}.
 * </p>
 */
@FeignClient(
        name = "mentorClient",
        url = "${integration.access-service.url}",
        configuration = {CommonFeignConfig.class}
)
public interface MentorClient {

    /**
     * Предоставляет доступ к курсу пользователю во внешнем сервисе.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param dto параметры предоставления доступа к курсу
     * @return ответ внешнего сервиса
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/access/course/get-access",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> getCourseAccessToUser(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody GetAccessRequest dto);

    /**
     * Закрывает доступ пользователя к курсу во внешнем сервисе.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param dto параметры предоставления доступа к курсу
     * @return ответ внешнего сервиса
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/access/course/delete-access",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> deleteCourseAccessToUser(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody GetAccessRequest dto);

    /**
     * Предоставляет доступ к модулю пользователю во внешнем сервисе.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param dto параметры предоставления доступа к курсу
     * @return ответ внешнего сервиса
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/access/module/get-access",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> getModuleAccessToUser(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody GetAccessRequest dto);

    /**
     * Закрывает доступ пользователя к модулю во внешнем сервисе.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param dto параметры предоставления доступа к модулю
     * @return ответ внешнего сервиса
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/access/module/delete-access",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> deleteModuleAccessToUser(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody GetAccessRequest dto);

    /**
     * Возвращает статистику прогресса по курсу для наставника по его идентификатору.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param mentorId идентификатор наставника
     * @param courseId идентификатор курса
     * @return ответ с телом
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/progress/course/{mentorId}/{courseId}/statistics",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<CourseProgressResponse> getCourseProgressByMentor(
            @RequestHeader("RqUId") String rqUId,
            @PathVariable Long mentorId,
            @PathVariable Long courseId);

    /**
     * Возвращает список прогресса всех учеников на указанном курсе для конкретного наставника.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param mentorId идентификатор наставника
     * @param courseId идентификатор курса
     * @return ответ со списком учеников
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/progress/course/{mentorId}/{courseId}/users",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<MenteeProgressDto>> getAllUsersAtCourse(
            @RequestHeader("RqUId") String rqUId,
            @PathVariable Long mentorId,
            @PathVariable Long courseId);

}
