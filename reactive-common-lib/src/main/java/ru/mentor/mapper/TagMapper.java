package ru.mentor.mapper;

import org.springframework.stereotype.Component;
import ru.mentor.common.Tag;
import ru.mentor.entity.CourseTagEntity;

@Component
public class TagMapper {

    public Tag toGrpcTagResponse(CourseTagEntity courseTagEntity) {
        return Tag.newBuilder()
                  .setId(courseTagEntity.getId())
                  .setName(courseTagEntity.getTagName())
                  .setIsActive(courseTagEntity.getIsActive())
                  .setCreatedAt(UtilMapper.buildTimestamp(courseTagEntity.getCreatedAt()))
                  .build();
    }
}
