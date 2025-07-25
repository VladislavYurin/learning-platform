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
 */
@RestController
@RequestMapping("/access")
@RequiredArgsConstructor
@Tag(name = "Работа с доступами к курсам и модулям")
@Slf4j
public class AccessController {

    private final AccessService accessService;

    @PostMapping("/course/get-access")
    public ResponseEntity<?> getCourseAccessToUser(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody GetAccessRequest request) {
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на добавление доступа юзеру [ ID = %d ] к курсу [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getUserId(),
                request.getCourseId(),
                request.getUserId()
        ));
        accessService.getCourseAccessToUser(rqUId, request);
        log.info(String.format(
                "[ RqUId = %s ] Успешно обработан запрос на добавление доступа юзеру [ ID = %d ] к курсу [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getUserId(),
                request.getCourseId(),
                request.getUserId()
        ));
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/course/delete-access")
    public ResponseEntity<?> deleteCourseAccessToUser(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody GetAccessRequest request) {
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на удаление доступа юзеру [ ID = %d ] к курсу [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getUserId(),
                request.getCourseId(),
                request.getUserId()
        ));
        accessService.deleteCourseAccessToUser(rqUId, request);
        log.info(String.format(
                "[ RqUId = %s ] Успешно обработан запрос на удаление доступа юзеру [ ID = %d ] к курсу [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getUserId(),
                request.getCourseId(),
                request.getUserId()
        ));
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/module/get-access")
    public ResponseEntity<?> getModuleAccessToUser(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody GetAccessRequest request) {
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на добавление доступа юзеру [ ID = %d ] к модулю [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getUserId(),
                request.getModuleId(),
                request.getUserId()
        ));
        accessService.getModuleAccessToUser(rqUId, request);
        log.info(String.format(
                "[ RqUId = %s ] Успешно обработан запрос на добавление доступа юзеру [ ID = %d ] к модулю [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getUserId(),
                request.getModuleId(),
                request.getUserId()
        ));
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/module/delete-access")
    public ResponseEntity<?> deleteModuleAccessToUser(
            @RequestHeader("RqUId") String rqUId,
            @RequestBody GetAccessRequest request) {
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на удаление доступа юзеру [ ID = %d ] к модулю [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getUserId(),
                request.getModuleId(),
                request.getUserId()
        ));
        accessService.deleteModuleAccessToUser(rqUId, request);
        log.info(String.format(
                "[ RqUId = %s ] Успешно обработан запрос на удаление доступа юзеру [ ID = %d ] к модулю [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getUserId(),
                request.getModuleId(),
                request.getUserId()
        ));
        return ResponseEntity.ok().body(null);
    }

}
