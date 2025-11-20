package ru.mentor.mapper;

import org.mapstruct.Mapper;
import ru.mentor.gateway.model.CourseProgressResponse;

@Mapper(componentModel = "spring")
public interface CourseProgressResponseMapper {
    CourseProgressResponse toApi(ru.mentor.dto.CourseProgressResponse commonCourseProgressResponse);
}
