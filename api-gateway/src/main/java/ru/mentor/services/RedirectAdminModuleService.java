package ru.mentor.services;

import org.springframework.data.domain.Page;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.PageSettings;

public interface RedirectAdminModuleService {

    ModuleDto getModuleById(Long moduleId);

    Page<ModuleDto> getAllModules(PageSettings pageSettings);

}
