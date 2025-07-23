package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
    @Operation(summary = "Добавление модуля, доступно только админу или ментору-автору курса")
    public ResponseEntity<ModuleDto> createModule(@RequestBody InnerCreateModuleRequest request) {
        return ResponseEntity.ok().body(moduleService.createModule(request));
    }

    @DeleteMapping("/{userId}/{courseId}/{moduleId}")
    @Operation(summary = "Удаление модуля по его ID, доступно только админу или ментору-автору курса")
    public ResponseEntity<?> deleteModule(
            @PathVariable Long userId,
            @PathVariable Long courseId,
            @PathVariable Long moduleId) {
        moduleService.deleteModule(userId, courseId, moduleId);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/{userId}/{courseId}/{moduleId}")
    @Operation(summary = "Получение модуля по его ID")
    public ResponseEntity<ModuleDto> getModuleById(
            @PathVariable Long userId,
            @PathVariable Long courseId,
            @PathVariable Long moduleId) {
        return ResponseEntity.ok().body(moduleService.getModuleById(userId, courseId, moduleId));
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Импорт модуля из Markdown файла")
    public ResponseEntity<ModuleDto> importModuleFromMarkdown(
            @RequestPart("file") MultipartFile file,
            @RequestBody InnerCreateModuleRequest request) {
        return ResponseEntity.ok(moduleService.importModuleFromFile(request, file));
    }

}
