package ru.mentor.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseDtoMapper {

    ru.mentor.dto.CourseDto toCommon(ru.mentor.gateway.model.CourseDto apiDto);

    ru.mentor.gateway.model.CourseDto toApi(ru.mentor.dto.CourseDto commonDto);
}
