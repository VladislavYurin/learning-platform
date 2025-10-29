package ru.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.gateway.api.ModuleControllerApi;
import ru.mentor.gateway.model.CreateModuleRequest;
import ru.mentor.gateway.model.ModuleDto;
import ru.mentor.mapper.ModuleDtoMapper;
import ru.mentor.services.RedirectModuleService;

/**
 * Контроллер для управления модулями образовательной платформы.
 * Предоставляет endpoints для создания, получения и управления модулями.
 */
@RestController
@RequiredArgsConstructor
public class ModuleController implements ModuleControllerApi {

    private final RedirectModuleService redirectModuleService;
    private final ModuleDtoMapper moduleDtoMapper;

    /**
     * Реализация ручки POST /module/create
     */
    @Override
    public ResponseEntity<ModuleDto> createModule(CreateModuleRequest createModuleRequest) {
        ru.mentor.dto.ModuleDto commonModuleDto = redirectModuleService.createModule(createModuleRequest);
        ModuleDto apiModuleDto = moduleDtoMapper.toApiDto(commonModuleDto);
        return ResponseEntity.ok(apiModuleDto);
    }

    /**
     * Реализация ручки DELETE /module/{courseId}/{moduleId}
     */
    @Override
    public ResponseEntity<ModuleDto> deleteModule(Long courseId, Long moduleId) {
        redirectModuleService.deleteModule(courseId, moduleId);
        return ResponseEntity.ok().body(null);
    }

    /**
     * Реализация ручки GET /module/{courseId}/{moduleId}
     */
    @Override
    public ResponseEntity<ModuleDto> getModuleById(Long courseId, Long moduleId) {
        ru.mentor.dto.ModuleDto commonModuleDto = redirectModuleService.getModuleById(courseId, moduleId);
        ModuleDto apiModuleDto = moduleDtoMapper.toApiDto(commonModuleDto);
        return ResponseEntity.ok(apiModuleDto);
    }

    /**
     * Реализация ручки POST /module/import
     */
    @Override
    public ResponseEntity<ModuleDto> importModuleFromMarkdown(MultipartFile file, CreateModuleRequest request) {
        ru.mentor.dto.ModuleDto commonModuleDto = redirectModuleService.importModuleFromFile(request, file);
        ModuleDto apiModuleDto = moduleDtoMapper.toApiDto(commonModuleDto);
        return ResponseEntity.ok(apiModuleDto);
    }
}
