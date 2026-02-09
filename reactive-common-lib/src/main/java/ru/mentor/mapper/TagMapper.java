package ru.mentor.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.ListCourseTagsResponse;
import ru.mentor.entity.CourseTagEntity;

@Mapper(componentModel = "spring",
        uses = UtilMapper.class,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    @Mapping(target = "name", source = "tagName")
    @Mapping(target = "createdAt",
            qualifiedByName = "localDateTimeToTimestamp")
    CourseTagResponse courseTagEntityToCourseTagResponse(CourseTagEntity courseTagEntity);

    default ListCourseTagsResponse toGrpcTagsListResponse(List<CourseTagEntity> courseTagEntityList) {
        return ListCourseTagsResponse.newBuilder()
                .addAllTags(courseTagEntityList
                        .stream()
                        .map(this::courseTagEntityToCourseTagResponse)
                        .toList())
                .build();
    }
}
