package ru.mentor.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.ModuleDto;
import ru.mentor.services.RedirectAdminModuleService;

/**
 * Контроллер управления модулями для администраторов.
 */
@RestController
@RequestMapping("/admin/module")
@RequiredArgsConstructor
@Tag(name = "Admin module management.", description = "Управление модулями и их содержимым для админов.")
public class AdminModuleController {

    private final RedirectAdminModuleService redirectAdminModuleService;

    /**
     * Возвращает модуль по ID.
     *
     * @param moduleId
     *         ID модуля
     *
     * @return {@link ModuleDto}
     */
    @Operation(
            summary = "Получить модуль по ID.",
            description = "Возвращает информацию о модуле по его идентификатору. Необходимы права администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о модуле",
                            content = @Content(schema = @Schema(implementation = ModuleDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @GetMapping("/{moduleId}")
    public ModuleDto getModuleById(@PathVariable Long moduleId) {
        return redirectAdminModuleService.getModuleById(moduleId);
    }

    /**
     * Возвращает все модули курса по его ID.
     *
     * @param courseId
     *         ID курса
     *
     * @return {@link ModuleDto}
     */
    @Operation(
            summary = "Получить все модули",
            description = "Возвращает все модули с постраничностью.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница модулей",
                            content = @Content(schema = @Schema(implementation = ModuleDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @GetMapping("/all")
    public Page<ModuleDto> getAllModules(
            @RequestParam int pageNumber,
            @RequestParam int pageSize
    ) {
        return redirectAdminModuleService.getAllModules(pageNumber, pageSize);
    }

}
