package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.dto.tag.CreateCourseTagRequest;
import ru.mentor.entity.CourseTagEntity;

@Mapper(componentModel = "spring",
        uses = UtilMapper.class,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseTagMapper {

    CourseTagDto toDto(CourseTagEntity tagEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "courseTags", ignore = true)
    CourseTagEntity toEntity(CreateCourseTagRequest request);

    @Mapping(target = "tagName", source = "name")
    @Mapping(target = "createdAt", source = "createdAt",
            qualifiedByName = "timestampToLocalDateTime")
    CourseTagDto fromGrpc(ru.mentor.common.CourseTagResponse src);
}
