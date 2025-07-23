package ru.mentor.services;

import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;

public interface RedirectModuleService {

    ModuleDto createModule(CreateModuleRequest request);

    ModuleDto getModuleById(Long courseId, Long moduleId);

}
