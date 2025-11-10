package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentor.dto.tag.CourseTagDto;

@Mapper(componentModel = "spring", uses = ProtoTimeMapper.class)
public interface TagGrpcMapper {

    @Mapping(target = "tagName", source = "name")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "isActive", source = "isActive")
    CourseTagDto fromGrpc(ru.mentor.common.CourseTagResponse src);
}
