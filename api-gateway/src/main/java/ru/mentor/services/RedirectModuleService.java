package ru.mentor.services;

import org.springframework.web.multipart.MultipartFile;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;

public interface RedirectModuleService {

    ModuleDto createModule(CreateModuleRequest request);

    ModuleDto getModuleById(Long courseId, Long moduleId);

    ModuleDto importModuleFromFile(CreateModuleRequest request, MultipartFile file);

    void deleteModule(Long courseId, Long moduleId);

}
