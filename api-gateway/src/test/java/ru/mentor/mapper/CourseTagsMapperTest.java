package ru.mentor.mapper;

import org.junit.jupiter.api.Test;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.DeleteCourseTagRequest;
import ru.mentor.common.GetAllCourseTagsRequest;
import ru.mentor.common.GetCourseTagRequest;
import ru.mentor.common.Header;
import ru.mentor.dto.tag.CreateCourseTagRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CourseTagsMapperTest {

    private static final Long USER_ID = 1L;
    private static final Long TAG_ID = 10L;
    private static final String TAG_NAME = "Spring";

    private final CourseTagsMapper courseTagsMapper = new CourseTagsMapper();

    @Test
    void constructGrpcCreateRequest_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-1").build();
        CreateCourseTagRequest request = CreateCourseTagRequest.builder()
                .tagName(TAG_NAME)
                .build();

        CreateCourseTagGrpcRequest result =
                courseTagsMapper.constructGrpcCreateRequest(header, USER_ID, request);

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(USER_ID.longValue(), result.getSenderId());
        assertEquals(TAG_NAME, result.getName());
    }

    @Test
    void constructGrpcDeleteRequest_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-2").build();

        DeleteCourseTagRequest result =
                courseTagsMapper.constructGrpcDeleteRequest(header, USER_ID, TAG_ID);

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(USER_ID.longValue(), result.getSenderId());
        assertEquals(TAG_ID.longValue(), result.getTagId());
    }

    @Test
    void constructGrpcGetRequest_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-3").build();

        GetCourseTagRequest result =
                courseTagsMapper.constructGrpcGetRequest(header, USER_ID, TAG_ID);

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(USER_ID.longValue(), result.getSenderId());
        assertEquals(TAG_ID.longValue(), result.getTagId());
    }

    @Test
    void constructAllCourseTagsRequest_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-4").build();

        GetAllCourseTagsRequest result =
                courseTagsMapper.constructAllCourseTagsRequest(header, USER_ID);

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(USER_ID.longValue(), result.getSenderId());
    }
}
