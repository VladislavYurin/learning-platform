package ru.mentor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class AccessController {

    private final AccessService accessService;

    @PostMapping("/course/get-access")
    public ResponseEntity<?> getCourseAccessToUser(@RequestBody @Valid GetAccessRequest request) {
        accessService.getCourseAccessToUser(request);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/module/get-access")
    public ResponseEntity<?> getModuleAccessToUser(@RequestBody @Valid GetAccessRequest request) {
        accessService.getModuleAccessToUser(request);
        return ResponseEntity.ok().body(null);
    }

}
