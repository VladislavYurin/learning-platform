package ru.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.gateway.api.AccessControllerApi;
import ru.mentor.gateway.model.CourseAccessRequest;
import ru.mentor.gateway.model.ModuleAccessRequest;
import ru.mentor.services.RedirectAccessService;

/**
 * Контроллер для управления доступами пользователей к курсам и модулям.
 * Предоставляет endpoints для выдачи и отзыва доступов.
 */
@RestController
@RequiredArgsConstructor
public class AccessController implements AccessControllerApi {

    private final RedirectAccessService redirectAccessService;

    /**
     * Реализация ручки POST /access/course/get-access
     */
    @Override
    public ResponseEntity<Void> giveCourseAccess(CourseAccessRequest courseAccessRequest) {
        return redirectAccessService.giveCourseAccess(courseAccessRequest);
    }

    /**
     * Реализация ручки POST /access/module/get-access
     */
    @Override
    public ResponseEntity<Void> giveModuleAccess(ModuleAccessRequest moduleAccessRequest) {
        return redirectAccessService.giveModuleAccess(moduleAccessRequest);
    }

    /**
     * Реализация ручки POST /access/course/delete-access
     */
    @Override
    public ResponseEntity<Void> revokeCourseAccess(CourseAccessRequest courseAccessRequest) {
        return redirectAccessService.revokeCourseAccess(courseAccessRequest);
    }

    /**
     * Реализация ручки POST /access/module/delete-access
     */
    @Override
    public ResponseEntity<Void> revokeModuleAccess(ModuleAccessRequest moduleAccessRequest) {
        return redirectAccessService.revokeModuleAccess(moduleAccessRequest);
    }
}
