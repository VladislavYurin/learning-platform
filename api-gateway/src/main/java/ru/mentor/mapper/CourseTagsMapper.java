package ru.mentor.mapper;

import org.springframework.stereotype.Component;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.DeleteCourseTagRequest;
import ru.mentor.common.GetCourseTagRequest;
import ru.mentor.common.Header;
import ru.mentor.dto.tag.CreateCourseTagRequest;

@Component
public class CourseTagsMapper {

    public CreateCourseTagGrpcRequest constructGrpcCreateRequest(
            Header header,
            Long userId,
            CreateCourseTagRequest request) {
        return CreateCourseTagGrpcRequest.newBuilder()
                                         .setHeader(header)
                                         .setSenderId(userId)
                                         .setName(request.getTagName())
                                         .build();
    }

    public DeleteCourseTagRequest constructGrpcDeleteRequest(Header header,
            Long userId, Long tagId){
        return DeleteCourseTagRequest.newBuilder()
                                     .setHeader(header)
                                     .setSenderId(userId)
                                     .setTagId(tagId)
                                     .build();
    }

    public GetCourseTagRequest constructGrpcGetRequest(Header header,
            Long userId, Long tagId){
        return GetCourseTagRequest.newBuilder()
                                  .setHeader(header)
                                  .setSenderId(userId)
                                  .setTagId(tagId)
                                  .build();
    }
}
