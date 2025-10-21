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
import ru.mentor.dto.*;

/**
 * Клиент OpenFeign для работы с курсами во внешнем сервисе.
 * <p>
 *      Инкапсулирует HTTP-вызовы API курсов: создание и удаление.
 *      Для трассировки запросов передаёт корелляционный заголовок {@code RqUId}.
 * </p>
 */
@FeignClient(
        name = "courseClient",
        url = "${integration.course-service.url}",
        configuration = {CommonFeignConfig.class}
)
public interface CourseClient {

    /**
     * Создает курс во внешнем сервисе.
     * @param rqUId корелляционный идентификатор запроса (попадает в заголовок {@code RqUId})
     * @param dto тело запроса для создания курса
     * @return созданный курс
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/course/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    CourseDto createCourse(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody InnerCreateCourseRequest dto);

    /**
     * Удаляет курс во внешнем сервисе.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param userId идентификатор пользователя-владельца курса
     * @param courseId идентификатор курса
     * @return ответ внешнего сервиса
     */
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

    /**
     * Возвращает курс по его идентификатору.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param userId идентификатор пользователя-владельца курса
     * @param courseId идентификатор курса
     * @return курс
     */
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

    /**
     * Возвращает список всех активных курсов.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param userId идентификатор пользователя-владельца курса
     * @return список курсов
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/course/{userId}/all/active",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<CourseDto> getAllActiveCourses(
            @RequestHeader("RqUId") String rqUId,
            @PathVariable Long userId);

    /**
     * Возвращает список всех курсов.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param userId идентификатор пользователя-владельца курса
     * @return список курсов
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/course/{userId}/all",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<CourseDto> getAllCourses(@RequestHeader("RqUId") String rqUId, @PathVariable Long userId);

    /**
     * Создает модуль во внешнем сервисе.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param request тело запроса на создание модуля (метаданные модуля)
     * @return созданный модуль
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/module/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ModuleDto createModule(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody InnerCreateModuleRequest request);

    /**
     * Импортирует модуль из Markdown-файла.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param request тело запроса на создание модуля (метаданные модуля)
     * @param file загружаемый Markdown-файл с содержимым модуля
     * @return модуль
     */
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

    /**
     * Удаляет модуль из внешнего сервиса.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param userId идентификатор пользователя-владельца курса
     * @param courseId идентификатор курса
     * @param moduleId идентификатор модуля внутри курса
     * @return ответ внешнего сервиса
     */
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

    /**
     * Возвращает модуль по его идентификатору.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @param userId идентификатор пользователя-владельца курса
     * @param courseId идентификатор курса
     ** @param moduleId идентификатор модуля
     * @return курс
     */
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

    /**
     * Возвращает список всех активных курсов без модулей c информацией о наставнике.
     * @param rqUId корелляционный идентификатор запроса (заголовок {@code RqUId})
     * @return список курсов
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/course/all/active/preview",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<CourseDtoWithoutModules> getAllActiveCoursesPreview(
            @RequestHeader("RqUId") String rqUId);

}
