package ru.mentor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.service.AccessService;

/**
 * Контроллер для работы с доступами со стороны ментора.
 * Управляет доступом пользователей к курсам и модулям.
 * <p>
 * Этот контроллер предоставляет API для добавления и удаления доступа к курсам и модулям
 * для конкретных пользователей. Использует аннотации Spring для определения маршрутов
 * и обработки HTTP запросов.
 */
@RestController
@RequestMapping("/access")
@RequiredArgsConstructor
@Tag(name = "Работа с доступами к курсам и модулям")
@Slf4j
public class AccessController {

    private final AccessService accessService;

    /**
     * Запрос на добавление доступа пользователя к курсу.
     *
     * @param requestId
     *         Идентификатор запроса, ассоциированный с текущей сессией.
     * @param request
     *         Объект запроса, содержащий идентификаторы пользователя и курса.
     *
     * @return Ответ с подтверждением успешного добавления доступа.
     */
    @PostMapping("/course/get-access")
    public ResponseEntity<?> getCourseAccessToUser(
            @RequestHeader("requestId") String requestId,
            @RequestBody GetAccessRequest request) {
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на добавление доступа юзеру [ ID = %d ] к курсу [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                request.getUserId(),
                request.getCourseId(),
                request.getUserId()
        ));
        accessService.getCourseAccessToUser(requestId, request);
        log.info(String.format(
                "[ requestId = %s ] Успешно обработан запрос на добавление доступа юзеру [ ID = %d ] к курсу [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                request.getUserId(),
                request.getCourseId(),
                request.getUserId()
        ));
        return ResponseEntity.ok().body(null);
    }

    /**
     * Запрос на удаление доступа пользователя к курсу.
     *
     * @param requestId
     *         Идентификатор запроса, ассоциированный с текущей сессией.
     * @param request
     *         Объект запроса, содержащий идентификаторы пользователя и курса.
     *
     * @return Ответ с подтверждением успешного удаления доступа.
     */
    @PostMapping("/course/delete-access")
    public ResponseEntity<?> deleteCourseAccessToUser(
            @RequestHeader("requestId") String requestId,
            @RequestBody GetAccessRequest request) {
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на удаление доступа юзеру [ ID = %d ] к курсу [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                request.getUserId(),
                request.getCourseId(),
                request.getUserId()
        ));
        accessService.deleteCourseAccessToUser(requestId, request);
        log.info(String.format(
                "[ requestId = %s ] Успешно обработан запрос на удаление доступа юзеру [ ID = %d ] к курсу [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                request.getUserId(),
                request.getCourseId(),
                request.getUserId()
        ));
        return ResponseEntity.ok().body(null);
    }

    /**
     * Запрос на добавление доступа пользователя к модулю.
     *
     * @param requestId
     *         Идентификатор запроса, ассоциированный с текущей сессией.
     * @param request
     *         Объект запроса, содержащий идентификаторы пользователя и модуля.
     *
     * @return Ответ с подтверждением успешного добавления доступа к модулю.
     */
    @PostMapping("/module/get-access")
    public ResponseEntity<?> getModuleAccessToUser(
            @RequestHeader("requestId") String requestId,
            @RequestBody GetAccessRequest request) {
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на добавление доступа юзеру [ ID = %d ] к модулю [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                request.getUserId(),
                request.getModuleId(),
                request.getUserId()
        ));
        accessService.getModuleAccessToUser(requestId, request);
        log.info(String.format(
                "[ requestId = %s ] Успешно обработан запрос на добавление доступа юзеру [ ID = %d ] к модулю [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                request.getUserId(),
                request.getModuleId(),
                request.getUserId()
        ));
        return ResponseEntity.ok().body(null);
    }

    /**
     * Запрос на удаление доступа пользователя к модулю.
     *
     * @param requestId
     *         Идентификатор запроса, ассоциированный с текущей сессией.
     * @param request
     *         Объект запроса, содержащий идентификаторы пользователя и модуля.
     *
     * @return Ответ с подтверждением успешного удаления доступа к модулю.
     */
    @PostMapping("/module/delete-access")
    public ResponseEntity<?> deleteModuleAccessToUser(
            @RequestHeader("requestId") String requestId,
            @RequestBody GetAccessRequest request) {
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на удаление доступа юзеру [ ID = %d ] к модулю [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                request.getUserId(),
                request.getModuleId(),
                request.getUserId()
        ));
        accessService.deleteModuleAccessToUser(requestId, request);
        log.info(String.format(
                "[ requestId = %s ] Успешно обработан запрос на удаление доступа юзеру [ ID = %d ] к модулю [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                request.getUserId(),
                request.getModuleId(),
                request.getUserId()
        ));
        return ResponseEntity.ok().body(null);
    }

}
