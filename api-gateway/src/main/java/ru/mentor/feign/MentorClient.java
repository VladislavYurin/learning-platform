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
 *     каждый запрос передаёт корелляционный заголовок {@code requestId}.
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
     * @param requestId корелляционный идентификатор запроса (заголовок {@code requestId})
     * @param dto параметры предоставления доступа к курсу
     * @return ответ внешнего сервиса
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/access/course/get-access",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> giveCourseAccess(
            @RequestHeader("requestId") String requestId,
            @RequestBody GetAccessRequest dto);

    /**
     * Закрывает доступ пользователя к курсу во внешнем сервисе.
     * @param requestId корелляционный идентификатор запроса (заголовок {@code requestId})
     * @param dto параметры предоставления доступа к курсу
     * @return ответ внешнего сервиса
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/access/course/delete-access",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> revokeCourseAccess(
            @RequestHeader("requestId") String requestId,
            @RequestBody GetAccessRequest dto);

    /**
     * Предоставляет доступ к модулю пользователю во внешнем сервисе.
     * @param requestId корелляционный идентификатор запроса (заголовок {@code requestId})
     * @param dto параметры предоставления доступа к курсу
     * @return ответ внешнего сервиса
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/access/module/get-access",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> giveModuleAccess(
            @RequestHeader("requestId") String requestId,
            @RequestBody GetAccessRequest dto);

    /**
     * Закрывает доступ пользователя к модулю во внешнем сервисе.
     * @param requestId корелляционный идентификатор запроса (заголовок {@code requestId})
     * @param dto параметры предоставления доступа к модулю
     * @return ответ внешнего сервиса
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/access/module/delete-access",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> revokeModuleAccess(
            @RequestHeader("requestId") String requestId,
            @RequestBody GetAccessRequest dto);

    /**
     * Возвращает статистику прогресса по курсу для наставника по его идентификатору.
     * @param requestId корелляционный идентификатор запроса (заголовок {@code requestId})
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
            @RequestHeader("requestId") String requestId,
            @PathVariable Long mentorId,
            @PathVariable Long courseId);

    /**
     * Возвращает список прогресса всех учеников на указанном курсе для конкретного наставника.
     * @param requestId корелляционный идентификатор запроса (заголовок {@code requestId})
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
            @RequestHeader("requestId") String requestId,
            @PathVariable Long mentorId,
            @PathVariable Long courseId);

}
