package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.DeleteCourseTagRequest;
import ru.mentor.common.GetAllCourseTagsRequest;
import ru.mentor.common.GetCourseTagRequest;
import ru.mentor.common.Header;
import ru.mentor.dto.tag.CreateCourseTagRequest;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseTagsMapper {

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "userId")
    @Mapping(target = "name", source = "request.tagName")
    CreateCourseTagGrpcRequest constructGrpcCreateRequest(
            Header header,
            Long userId,
            CreateCourseTagRequest request);

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "userId")
    @Mapping(target = "tagId", source = "tagId")
    DeleteCourseTagRequest constructGrpcDeleteRequest(Header header,
            Long userId, Long tagId);

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "userId")
    @Mapping(target = "tagId", source = "tagId")
    GetCourseTagRequest constructGrpcGetRequest(Header header,
            Long userId, Long tagId);

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "userId")
    GetAllCourseTagsRequest constructAllCourseTagsRequest(Header header, Long userId);
}
