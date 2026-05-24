package ru.mentor.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllActiveCoursesResponse;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.AuthorResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.GetAllActiveCoursesPreviewRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.dto.front.CreateCourseRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseMapperTest {

    @Mock
    private BaseMapper baseMapper;

    @Mock
    private AdminCourseMapper adminCourseMapper;

    @Mock
    private TagGrpcMapper tagGrpcMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CourseMapper courseMapper = new CourseMapperImpl();

    private static final Long USER_ID = 1L;
    private static final Long COURSE_ID = 10L;

    @Test
    void constructGrpcCreateRequest_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-1").build();

        CreateCourseRequest request = CreateCourseRequest.builder()
                .courseName("Course")
                .courseDescription("Description")
                .tagIds(List.of(1L, 2L, 3L))
                .build();

        CreateCourseGrpcRequest result = courseMapper.constructGrpcCreateRequest(header, USER_ID, request);

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(USER_ID.longValue(), result.getUserId());
        assertEquals("Course", result.getCourseName());
        assertEquals("Description", result.getCourseDescription());
        assertEquals(List.of(1L, 2L, 3L), result.getTagIdsList());
    }

    @Test
    void constructGrpcGetRequest_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-2").build();

        GetCourseRequest result = courseMapper.constructGrpcGetRequest(header, USER_ID, COURSE_ID);

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(USER_ID.longValue(), result.getSenderId());
        assertEquals(COURSE_ID.longValue(), result.getCourseId());
    }

    @Test
    void constructGetAllActiveCoursesPreviewRequest_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-3").build();

        GetAllActiveCoursesPreviewRequest result =
                courseMapper.constructGetAllActiveCoursesPreviewRequest(header, USER_ID);

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(USER_ID.longValue(), result.getSenderId());
    }

    @Test
    void constructGrpcDeleteRequest_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-4").build();

        DeleteCourseRequest result = courseMapper.constructGrpcDeleteRequest(header, USER_ID, COURSE_ID);

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(USER_ID.longValue(), result.getSenderId());
        assertEquals(COURSE_ID.longValue(), result.getCourseId());
    }

    @Test
    void constructGrpcPageRequest_delegatesToBaseMapper() {
        Header header = Header.newBuilder().setRequestId("rq-5").build();
        int pageNumber = 1;
        int pageSize = 20;

        GrpcPageRequest expected = GrpcPageRequest.newBuilder().build();
        when(baseMapper.constructGrpcPageRequest(header, pageNumber, pageSize, USER_ID))
                .thenReturn(expected);

        GrpcPageRequest result = courseMapper.constructGrpcPageRequest(header, pageNumber, pageSize, USER_ID);

        assertSame(expected, result);
        verify(baseMapper).constructGrpcPageRequest(header, pageNumber, pageSize, USER_ID);
    }

    @Test
    void mapGrpcCourseResponseToCourseDto_delegatesToAdminCourseMapper() {
        CourseResponse response = CourseResponse.newBuilder().setCourseId(COURSE_ID).build();
        CourseDto expected = CourseDto.builder().id(COURSE_ID).build();
        when(adminCourseMapper.mapGrpcCourseResponseToCourseDto(response)).thenReturn(expected);

        CourseDto result = courseMapper.mapGrpcCourseResponseToCourseDto(response);

        assertSame(expected, result);
        verify(adminCourseMapper).mapGrpcCourseResponseToCourseDto(response);
    }

    @Test
    void mapGrpcCourseResponseToCourseDtoPage_delegatesToAdminCourseMapper() {
        AllCoursesResponse allCoursesResponse = AllCoursesResponse.newBuilder().build();
        @SuppressWarnings("unchecked")
        Page<CourseDto> expectedPage = (Page<CourseDto>) mock(Page.class);
        when(adminCourseMapper.mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse))
                .thenReturn(expectedPage);

        Page<CourseDto> result = courseMapper.mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse);

        assertSame(expectedPage, result);
        verify(adminCourseMapper).mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse);
    }

    @Test
    void mapGrpcAllActiveCoursesResponseToCourseDtoList_sortsByTitleAndMapsFields() {
        Instant now = Instant.now();

        var timestamp = com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();

        CourseTagResponse grpcTag = CourseTagResponse.newBuilder()
                .setId(1L)
                .setName("tag1")
                .build();

        AuthorResponse grpcAuthor = AuthorResponse.newBuilder()
                .setUserId(5L)
                .setFirstName("John")
                .setLastName("Doe")
                .build();

        CourseResponse courseB = CourseResponse.newBuilder()
                .setCourseId(2L)
                .setTitle("B course")
                .setDescription("Desc B")
                .setCreatedAt(timestamp)
                .setAuthor(grpcAuthor)
                .addTags(grpcTag)
                .build();

        CourseResponse courseA = CourseResponse.newBuilder()
                .setCourseId(1L)
                .setTitle("A course")
                .setDescription("Desc A")
                .setCreatedAt(timestamp)
                .setAuthor(grpcAuthor)
                .addTags(grpcTag)
                .build();

        AllActiveCoursesResponse response = AllActiveCoursesResponse.newBuilder()
                .addCourses(courseB)
                .addCourses(courseA)
                .build();

        CourseTagDto tagDto = CourseTagDto.builder().id(1L).tagName("tag1").build();
        when(tagGrpcMapper.fromGrpc(any())).thenReturn(tagDto);
        when(userMapper.mapGrpcAuthorResponseToUserInfoDto(any()))
                .thenReturn(UserInfoDto.builder().id(5L).firstName("John").lastName("Doe").build());

        List<CourseDto> result = courseMapper.mapGrpcAllActiveCoursesResponseToCourseDtoList(response);

        assertEquals(2, result.size());
        assertEquals("A course", result.get(0).getCourseTitle());
        assertEquals("B course", result.get(1).getCourseTitle());

        LocalDateTime expectedCreatedAt = LocalDateTime.ofEpochSecond(
                now.getEpochSecond(),
                now.getNano(),
                ZoneOffset.UTC
        );

        assertEquals(expectedCreatedAt, result.get(0).getCreatedAt());
        assertEquals(expectedCreatedAt, result.get(1).getCreatedAt());
        assertEquals(List.of(tagDto), result.get(0).getTags());
        assertEquals(5L, result.get(0).getAuthor().getId());
    }
}

