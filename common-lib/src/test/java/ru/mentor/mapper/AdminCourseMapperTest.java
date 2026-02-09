package ru.mentor.mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.AuthorResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.Header;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@SpringBootTest(classes = {
        AdminCourseMapperImpl.class,
        UtilMapperImpl.class})
class AdminCourseMapperTest {

    @Autowired
    private AdminCourseMapper adminCourseMapper;

    @Test
    void courseResponseToCourseDto_success() {
        UserInfoDto userInfoDto = TestEntityStubGenerator.getMentorInfoDto();
        CourseResponse grpcResponse = TestGrpcStubGenerator.constructCourseResponse();

        CourseDto dto = adminCourseMapper.courseResponseToCourseDto(grpcResponse);

        Assertions.assertThat(dto.getId()).isEqualTo(TestConstantHolder.courseId);
        Assertions.assertThat(dto.getCourseTitle()).isEqualTo(TestConstantHolder.courseTitle);
        Assertions.assertThat(dto.getCourseDescription())
                .isEqualTo(TestConstantHolder.courseDescription);
        Assertions.assertThat(dto.getAuthor()).isEqualTo(userInfoDto);
    }

    @Test
    void allCoursesResponseToCourseDtoPage_success() {
        AllCoursesResponse grpcAllResponse = TestGrpcStubGenerator.constructAllCoursesResponse();

        Page<CourseDto> result = adminCourseMapper.allCoursesResponseToCourseDtoPage(
                grpcAllResponse);

        Assertions.assertThat(result.getContent()).hasSize(TestConstantHolder.totalElementsCount);
        Assertions.assertThat(result.getContent().get(TestConstantHolder.zero).getId())
                .isEqualTo(TestConstantHolder.courseId);
    }

    @Test
    void courseEntityToCourseResponse_success() {
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        AuthorResponse authorResponse = TestGrpcStubGenerator.constructAuthorResponse();

        CourseResponse grpcResponse = adminCourseMapper.courseEntityToCourseResponse(courseEntity);

        Assertions.assertThat(grpcResponse.getCourseId()).isEqualTo(TestConstantHolder.courseId);
        Assertions.assertThat(grpcResponse.getTitle()).isEqualTo(TestConstantHolder.courseTitle);
        Assertions.assertThat(grpcResponse.getAuthor()).isEqualTo(authorResponse);
    }

    @Test
    void courseEntityPageToAllCoursesResponse_success() {
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        AuthorResponse authorResponse = TestGrpcStubGenerator.constructAuthorResponse();

        Page<CourseEntity> coursePage = TestEntityStubGenerator.constructCourseEntityPage(
                courseEntity);
        AllCoursesResponse response =
                adminCourseMapper.courseEntityPageToAllCoursesResponse(coursePage);

        Assertions.assertThat(response.getCoursesList())
                .hasSize(TestConstantHolder.totalElementsCount);
        Assertions.assertThat(response.getCoursesList().get(TestConstantHolder.zero)
                .getCourseId()).isEqualTo(TestConstantHolder.courseId);
    }

    @Test
    void toGetCourseRequest_success() {
        Header header = TestGrpcStubGenerator.constructHeader();

        GetCourseRequest request = adminCourseMapper.toGetCourseRequest(
                header, TestConstantHolder.courseId);

        Assertions.assertThat(request.getHeader().getRequestId())
                .isEqualTo(TestConstantHolder.requestId);
        Assertions.assertThat(request.getCourseId()).isEqualTo(TestConstantHolder.courseId);
    }

}
