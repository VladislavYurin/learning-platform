package ru.mentor.mapper;

import org.mapstruct.Mapper;
import ru.mentor.gateway.model.CourseTagDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateTimeMapper.class)
public interface CourseTagDtoMapper {
    CourseTagDto toApiDto(ru.mentor.dto.tag.CourseTagDto commonCourseTagDto);
    List<CourseTagDto> toListApiDto(List<ru.mentor.dto.tag.CourseTagDto> commonCourseTagDto);
}
