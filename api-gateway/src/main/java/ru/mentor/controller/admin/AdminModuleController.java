package ru.mentor.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.gateway.model.ModuleDto;
import ru.mentor.gateway.api.AdminModuleControllerApi;
import ru.mentor.gateway.model.PageModuleDto;
import ru.mentor.mapper.ModuleDtoMapper;
import ru.mentor.mapper.PageModuleDtoMapper;
import ru.mentor.services.RedirectAdminModuleService;

/**
 * Контроллер управления модулями для администраторов.
 */
@RestController
@RequiredArgsConstructor
public class AdminModuleController implements AdminModuleControllerApi {

    private final RedirectAdminModuleService redirectAdminModuleService;
    private final ModuleDtoMapper moduleDtoMapper;
    private final PageModuleDtoMapper pageModuleDtoMapper;

    /**
     * Реализация ручки GET /admin/module/{moduleId}
     */
    @Override
    public ResponseEntity<ModuleDto> adminGetModuleById(Long moduleId) {
        ru.mentor.dto.ModuleDto moduleDto = redirectAdminModuleService.getModuleById(moduleId);
        return ResponseEntity.ok(moduleDtoMapper.toApiDto(moduleDto));
    }

    /**
     * Реализация ручки GET /admin/module/all
     */
    @Override
    public ResponseEntity<PageModuleDto> adminGetAllModules(Integer pageNumber, Integer pageSize) {
        Page<ru.mentor.dto.ModuleDto> page = redirectAdminModuleService.getAllModules(pageNumber, pageSize);
        return ResponseEntity.ok(pageModuleDtoMapper.toApiDto(page));
    }
}
