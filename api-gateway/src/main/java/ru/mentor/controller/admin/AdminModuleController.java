package ru.mentor.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.gateway.model.ModuleDto;
import ru.mentor.gateway.api.AdminModuleControllerApi;
import ru.mentor.mapper.ModuleDtoMapper;
import ru.mentor.services.RedirectAdminModuleService;

/**
 * Контроллер управления модулями для администраторов.
 */
@RestController
@RequiredArgsConstructor
public class AdminModuleController implements AdminModuleControllerApi {

    private final RedirectAdminModuleService redirectAdminModuleService;
    private final ModuleDtoMapper moduleDtoMapper;

    /**
     * Реализация ручки GET /admin/module/get-one
     */
    @Override
    public ResponseEntity<ModuleDto> adminGetModuleById(Long moduleId) {
        ru.mentor.dto.ModuleDto moduleDto = redirectAdminModuleService.getModuleById(moduleId);
        return ResponseEntity.ok(moduleDtoMapper.toApiDto(moduleDto));
    }
}
