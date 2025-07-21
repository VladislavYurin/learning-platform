package ru.mentor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.InnerCreateModuleRequest;
import ru.mentor.dto.ModuleDto;
import ru.mentor.service.ModuleService;

/**
 * Контроллер для работы с курсами.
 */
@RestController
@RequestMapping("/module")
@RequiredArgsConstructor
@Tag(name = "Работа с модулями")
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping("/create")
    public ResponseEntity<ModuleDto> createModule(@RequestBody @Valid InnerCreateModuleRequest request) {
        return ResponseEntity.ok().body(moduleService.createModule(request));
    }

    @DeleteMapping("/{userId}/{courseId}/{moduleId}")
    public ResponseEntity<?> deleteModule(@PathVariable Long userId, @PathVariable Long courseId, @PathVariable Long moduleId) {
        moduleService.deleteModule(userId, courseId, moduleId);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/{userId}/{courseId}/{moduleId}")
    public ResponseEntity<ModuleDto> getModuleById(@PathVariable Long userId, @PathVariable Long courseId, @PathVariable Long moduleId) {
        return ResponseEntity.ok().body(moduleService.getModuleById(userId, courseId, moduleId));
    }

}
