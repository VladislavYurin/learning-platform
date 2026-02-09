package ru.mentor.mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllActiveCoursesResponse;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.GetAllActiveCoursesPreviewRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.front.CreateCourseRequest;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestGrpcStubGenerator;

import java.util.List;

@SpringBootTest(classes = {
        AdminCourseMapperImpl.class,
        BaseMapperImpl.class,
        CourseMapper.class,
        CourseTagMapperImpl.class,
        UtilMapperImpl.class
})
class CourseMapperTest {
    @Autowired
    private CourseMapper courseMapper;

    @Test
    void toToCreateCourseGrpcRequest_success() {
        List<Long> tagIdsList = List.of(1L, 2L, 3L);
        CreateCourseRequest createCourseRequest = CreateCourseRequest.builder()
                .authorId(TestConstantHolder.authorId)
                .courseName(TestConstantHolder.courseTagName)
                .courseDescription(TestConstantHolder.courseDescription)
                .tagIds(tagIdsList)
                .build();

        CreateCourseGrpcRequest grpcRequest = courseMapper.toCreateCourseGrpcRequest(
                TestGrpcStubGenerator.constructHeader(),
                TestConstantHolder.userId,
                createCourseRequest);

        Assertions.assertThat(grpcRequest.getHeader().getRequestId())
                .isEqualTo(TestConstantHolder.requestId);
        Assertions.assertThat(grpcRequest.getUserId())
                .isEqualTo(TestConstantHolder.userId);
        Assertions.assertThat(grpcRequest.getCourseName())
                .isEqualTo(TestConstantHolder.courseTagName);
        Assertions.assertThat(grpcRequest.getCourseDescription())
                .isEqualTo(TestConstantHolder.courseDescription);
        Assertions.assertThat(grpcRequest.getTagIdsList())
                .containsExactlyElementsOf(tagIdsList);
    }

    @Test
    void toGetCourseRequest_success() {
        GetCourseRequest request = courseMapper.toGetCourseRequest(
                TestGrpcStubGenerator.constructHeader(),
                TestConstantHolder.userId,
                TestConstantHolder.courseId
        );

        Assertions.assertThat(request.getHeader().getRequestId())
                .isEqualTo(TestConstantHolder.requestId);
        Assertions.assertThat(request.getSenderId())
                .isEqualTo(TestConstantHolder.userId);
        Assertions.assertThat(request.getCourseId())
                .isEqualTo(TestConstantHolder.courseId);
    }

    @Test
    void toGetAllActiveCoursesPreviewRequest_success() {
        GetAllActiveCoursesPreviewRequest request = courseMapper.toGetAllActiveCoursesPreviewRequest(
                TestGrpcStubGenerator.constructHeader(),
                TestConstantHolder.userId
        );

        Assertions.assertThat(request.getHeader().getRequestId())
                .isEqualTo(TestConstantHolder.requestId);
        Assertions.assertThat(request.getSenderId())
                .isEqualTo(TestConstantHolder.userId);
    }

    @Test
    void toDeleteCourseRequest_success() {
        DeleteCourseRequest request = courseMapper.toDeleteCourseRequest(
                TestGrpcStubGenerator.constructHeader(),
                TestConstantHolder.userId,
                TestConstantHolder.courseId
        );

        Assertions.assertThat(request.getHeader().getRequestId())
                .isEqualTo(TestConstantHolder.requestId);
        Assertions.assertThat(request.getSenderId())
                .isEqualTo(TestConstantHolder.userId);
        Assertions.assertThat(request.getCourseId())
                .isEqualTo(TestConstantHolder.courseId);
    }

    @Test
    void toGrpcPageRequest_success() {
        GrpcPageRequest request = courseMapper.toGrpcPageRequest(
                TestGrpcStubGenerator.constructHeader(),
                TestConstantHolder.pageNumber,
                TestConstantHolder.pageSize,
                TestConstantHolder.userId
        );

        Assertions.assertThat(request.getHeader().getRequestId())
                .isEqualTo(TestConstantHolder.requestId);
        Assertions.assertThat(request.getPageNumber())
                .isEqualTo(TestConstantHolder.pageNumber);
        Assertions.assertThat(request.getPageSize())
                .isEqualTo(TestConstantHolder.pageSize);
        Assertions.assertThat(request.getSenderId())
                .isEqualTo(TestConstantHolder.userId);
    }

    @Test
    void mapGrpcCourseResponseToCourseDto_success() {
        CourseResponse response = TestGrpcStubGenerator.constructCourseResponse();

        CourseDto courseDto = courseMapper.mapGrpcCourseResponseToCourseDto(response);

        Assertions.assertThat(courseDto.getId())
                .isEqualTo(TestConstantHolder.courseId);
        Assertions.assertThat(courseDto.getCourseTitle())
                .isEqualTo(TestConstantHolder.courseTitle);
        Assertions.assertThat(courseDto.getCourseDescription())
                .isEqualTo(TestConstantHolder.courseDescription);
        Assertions.assertThat(courseDto.getIsActive())
                .isEqualTo(TestConstantHolder.isActiveFalse);
        Assertions.assertThat(courseDto.getCreatedAt())
                .isEqualTo(TestConstantHolder.createdAt);
        Assertions.assertThat(courseDto.getAuthor().getId())
                .isEqualTo(TestConstantHolder.mentorId);
    }

    @Test
    void mapGrpcCourseResponseToCourseDtoPage_success() {
        AllCoursesResponse allActiveCourses = TestGrpcStubGenerator.constructAllCoursesResponse();

        Page<CourseDto> courseDtoPage = courseMapper.mapGrpcCourseResponseToCourseDtoPage(allActiveCourses);

        Assertions.assertThat(courseDtoPage).isNotNull();
        Assertions.assertThat(courseDtoPage.getTotalElements())
                .isEqualTo(1);  // ожидаем 1 элемент
        Assertions.assertThat(courseDtoPage.getTotalPages())
                .isEqualTo(1);  // ожидаем 1 страницу
        Assertions.assertThat(courseDtoPage.getContent())
                .hasSize(1);
    }

    @Test
    void mapGrpcAllActiveCoursesResponseToCourseDtoList_success() {
        // дописать
        CourseResponse courseResponse = TestGrpcStubGenerator.constructCourseResponse();
        AllActiveCoursesResponse response = AllActiveCoursesResponse.newBuilder()
                .addAllCourses(List.of(courseResponse))
                .build();

        List<CourseDto> courses = courseMapper.mapGrpcAllActiveCoursesResponseToCourseDtoList(response);

        Assertions.assertThat(courses.getFirst().getId())
                .isEqualTo(TestConstantHolder.courseId);
    }

    @Test
    void mapCourseResponseToDto_success() {
        CourseResponse courseResponse = TestGrpcStubGenerator.constructCourseResponse();

        CourseDto courseDto = courseMapper.mapCourseResponseToDto(courseResponse);

        Assertions.assertThat(courseDto.getId())
                .isEqualTo(TestConstantHolder.courseId);
        Assertions.assertThat(courseDto.getCourseTitle())
                .isEqualTo(TestConstantHolder.courseTitle);
        Assertions.assertThat(courseDto.getCourseDescription())
                .isEqualTo(TestConstantHolder.courseDescription);
        Assertions.assertThat(courseDto.getIsActive())
                .isEqualTo(TestConstantHolder.isActiveTrue);
        Assertions.assertThat(courseDto.getCreatedAt())
                .isEqualTo(TestConstantHolder.createdAt);
        Assertions.assertThat(courseDto.getAuthor().getId())
                .isEqualTo(TestConstantHolder.mentorId);
    }

}