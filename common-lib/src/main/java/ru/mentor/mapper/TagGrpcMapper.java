package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.mentor.dto.tag.CourseTagDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = ProtoTimeMapper.class)
public interface TagGrpcMapper {

    @Mapping(target = "tagName", source = "name")
    CourseTagDto fromGrpc(ru.mentor.common.CourseTagResponse src);

    @Named("toCourseTagDto")
    List<CourseTagDto> toCourseTagDto(List<ru.mentor.common.CourseTagResponse> tagsList);

}
