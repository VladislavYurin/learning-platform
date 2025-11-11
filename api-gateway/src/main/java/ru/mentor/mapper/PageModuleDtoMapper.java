package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.mentor.dto.ModuleDto;
import ru.mentor.gateway.model.PageModuleDto;

@Mapper(componentModel = "spring", uses = { DateTimeMapper.class, SortMapper.class })
public interface PageModuleDtoMapper {
    PageModuleDto toApiDto(Page<ModuleDto> page);
}
