package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;
import ru.mentor.services.RedirectModuleService;
import ru.mentor.validation.ValidMarkdownFile;

/**
 * Контроллер для управления модулями образовательной платформы.
 * Предоставляет endpoints для создания, получения и управления модулями.
 */
@RestController
@RequestMapping("/module")
@RequiredArgsConstructor
@Tag(name = "Module Management", description = "Управление модулями и их содержимым")
public class ModuleController {

    private final RedirectModuleService redirectModuleService;

    @Operation(
            summary = "Создать модуль",
            description = "Позволяет создать новый модуль. Требуются права ADMIN или MENTOR",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Модуль успешно создан",
                            content = @Content(schema = @Schema(implementation = ModuleDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<?> createModule(@Valid @RequestBody CreateModuleRequest request) {
        return ResponseEntity.ok().body(redirectModuleService.createModule(request));
    }

    @Operation(
            summary = "Удалить модуль",
            description = "Позволяет удалить модуль. Требуются права ADMIN или MENTOR",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Модуль успешно удален",
                            content = @Content(schema = @Schema(implementation = ModuleDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @DeleteMapping("/{courseId}/{moduleOrderNum}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<Void> deleteModule(
            @PathVariable Long courseId,
            @PathVariable Integer moduleOrderNum) {
        redirectModuleService.deleteModule(courseId, moduleOrderNum);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Получить модуль по ID",
            description = "Возвращает информацию о модуле по его идентификатору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о модуле",
                            content = @Content(schema = @Schema(implementation = ModuleDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @GetMapping("/{courseId}/{moduleOrderNum}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR') or hasRole('USER')")
    public ModuleDto getModuleByOrderNum(
            @PathVariable Long courseId,
            @PathVariable Integer moduleOrderNum) {
        return redirectModuleService.getModuleByOrderNum(courseId, moduleOrderNum);
    }

    @Operation(
            summary = "Импорт модуля из Markdown файла",
            description = "Позволяет создать новый модуль из .md файла. Требуются права ADMIN или MENTOR",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о модуле",
                            content = @Content(schema = @Schema(implementation = ModuleDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<?> importModuleFromMarkdown(
            @RequestPart("file") @Valid @ValidMarkdownFile MultipartFile file,
            @RequestBody CreateModuleRequest request) {
        return ResponseEntity.ok(redirectModuleService.importModuleFromFile(request, file));
    }

}
