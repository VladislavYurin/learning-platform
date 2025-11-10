package ru.mentor.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.ListCourseTagsResponse;
import ru.mentor.entity.CourseTagEntity;

@Component
public class TagMapper {

    public CourseTagResponse toGrpcTagResponse(CourseTagEntity courseTagEntity) {
        return CourseTagResponse.newBuilder()
                                .setId(courseTagEntity.getId())
                                .setName(courseTagEntity.getTagName())
                                .setIsActive(courseTagEntity.getIsActive())
                                .setCreatedAt(UtilMapper.buildTimestamp(courseTagEntity.getCreatedAt()))
                                .build();
    }

    public ListCourseTagsResponse toGrpcTagsListResponse(List<CourseTagEntity> courseTagEntityList) {
        return ListCourseTagsResponse.newBuilder()
                                     .addAllTags(courseTagEntityList
                                                         .stream()
                                                         .map(this::toGrpcTagResponse)
                                                         .toList())
                                     .build();
    }
}
