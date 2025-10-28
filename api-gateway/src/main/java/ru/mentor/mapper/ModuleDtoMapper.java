package ru.mentor.mapper;

import org.mapstruct.Mapper;
import ru.mentor.gateway.model.ModuleDto;

@Mapper(componentModel = "spring")
public interface ModuleDtoMapper {
    ModuleDto toDto(ru.mentor.dto.ModuleDto moduleDto);
}
