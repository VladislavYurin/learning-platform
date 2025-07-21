package ru.mentor.service;

import ru.mentor.dto.InnerCreateModuleRequest;
import ru.mentor.dto.ModuleDto;

public interface ModuleService {

    ModuleDto createModule(InnerCreateModuleRequest request);

    void deleteModule(Long userId, Long courseId, Long moduleId);

    ModuleDto getModuleById(Long userId, Long courseId, Long moduleId);

}
