package ru.mentor.mapper;

import java.util.List;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.ListCourseTagsResponse;
import ru.mentor.entity.CourseTagEntity;

@Mapper(componentModel = "spring",
        uses = UtilMapper.class,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    @Named("toGrpcTagResponse")
    @Mapping(target = "name", source = "tagName")
    @Mapping(target = "createdAt",
             qualifiedByName = "buildTimestamp")
    @Mapping(target = "isActive", source = "isActive")
    CourseTagResponse toGrpcTagResponse(CourseTagEntity courseTagEntity);

    @Named("toGrpcTagResponseList")
    @IterableMapping(qualifiedByName = "toGrpcTagResponse")
    List<CourseTagResponse> toGrpcTagResponseList(List<CourseTagEntity> courseTagEntityList);

    default ListCourseTagsResponse toGrpcTagsListResponse(List<CourseTagEntity> courseTagEntityList) {
        return ListCourseTagsResponse.newBuilder()
                .addAllTags(courseTagEntityList
                        .stream()
                        .map(this::toGrpcTagResponse)
                        .toList())
                .build();
    }
}
