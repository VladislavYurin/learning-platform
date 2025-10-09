package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentor.dto.tag.CreateTagRequest;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.entity.CourseTagEntity;

@Mapper(componentModel = "spring")
public interface CourseTagMapper {

    CourseTagDto toDto(CourseTagEntity tagEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "courseTags", ignore = true)
    CourseTagEntity toEntity(CreateTagRequest request);
}
