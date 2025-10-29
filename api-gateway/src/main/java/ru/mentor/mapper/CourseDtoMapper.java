package ru.mentor.mapper;

import org.mapstruct.Mapper;
import ru.mentor.dto.CourseDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseDtoMapper {

    ru.mentor.dto.CourseDto toCommon(ru.mentor.gateway.model.CourseDto apiDto);

    ru.mentor.gateway.model.CourseDto toApi(ru.mentor.dto.CourseDto commonDto);

    List<ru.mentor.gateway.model.CourseDto> toApiList(List<CourseDto> list);

    List<ru.mentor.dto.CourseDto> toCommonList(List<ru.mentor.gateway.model.CourseDto> list);
}
