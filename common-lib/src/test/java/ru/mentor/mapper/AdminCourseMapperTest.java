package ru.mentor.mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.AuthorResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.grpc.HeaderFactory;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class AdminCourseMapperTest {

    @Mock
    private HeaderFactory headerFactory;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private AdminCourseMapper adminCourseMapper;

    @Test
    void mapGrpcCourseResponseToCourseDto_success() {
        UserInfoDto userInfoDto = TestEntityStubGenerator.constructUserInfoDtoWithRole(Role.MENTOR);
        CourseResponse grpcResponse = TestGrpcStubGenerator.constructCourseResponse();

        CourseDto dto = adminCourseMapper.mapGrpcCourseResponseToCourseDto(grpcResponse);

        Assertions.assertThat(dto.getId()).isEqualTo(TestConstantHolder.courseId);
        Assertions.assertThat(dto.getCourseTitle()).isEqualTo(TestConstantHolder.courseTitle);
        Assertions.assertThat(dto.getCourseDescription())
                  .isEqualTo(TestConstantHolder.courseDescription);
        Assertions.assertThat(dto.getAuthor()).isEqualTo(userInfoDto);
    }

    @Test
    void mapGrpcCourseResponseToCourseDtoPage_success() {
        AllCoursesResponse grpcAllResponse = TestGrpcStubGenerator.constructAllCoursesResponse();

        Page<CourseDto> result = adminCourseMapper.mapGrpcCourseResponseToCourseDtoPage(
                grpcAllResponse);

        Assertions.assertThat(result.getContent()).hasSize(TestConstantHolder.totalElementsCount);
        Assertions.assertThat(result.getContent().get(TestConstantHolder.pageNumber).getId())
                  .isEqualTo(TestConstantHolder.courseId);
    }

    @Test
    void mapCourseEntityToGrpcCourseResponse_success() {
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        AuthorResponse authorResponse = TestGrpcStubGenerator.constructAuthorResponse();

        CourseResponse grpcResponse = adminCourseMapper.mapCourseEntityToGrpcCourseResponse(
                courseEntity);

        Assertions.assertThat(grpcResponse.getCourseId()).isEqualTo(TestConstantHolder.courseId);
        Assertions.assertThat(grpcResponse.getTitle()).isEqualTo(TestConstantHolder.courseTitle);
        Assertions.assertThat(grpcResponse.getAuthor()).isEqualTo(authorResponse);
    }

    @Test
    void mapCourseEntityPageToGrpcAllCoursesResponse_success() {
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        AuthorResponse authorResponse = TestGrpcStubGenerator.constructAuthorResponse();

        Page<CourseEntity> coursePage = TestEntityStubGenerator.constructCourseEntityPage(courseEntity);
        AllCoursesResponse response =
                adminCourseMapper.mapCourseEntityPageToGrpcAllCoursesResponse(coursePage);

        Assertions.assertThat(response.getCoursesList()).hasSize(TestConstantHolder.totalElementsCount);
        Assertions.assertThat(response.getCoursesList().get(TestConstantHolder.pageNumber)
                                      .getCourseId()).isEqualTo(TestConstantHolder.courseId);
    }

    @Test
    void constructGetCourseRequest_success() {
        Mockito.when(headerFactory.create(Mockito.anyString()))
                .thenAnswer(inv -> ru.mentor.common.Header.newBuilder()
                        .setRequestId(inv.getArgument(0, String.class))
                        .setNodeId("test-node")
                        .setApiKey("test-api")
                        .build());

        GetCourseRequest request = adminCourseMapper.constructGetCourseRequest(
                TestConstantHolder.requestId, TestConstantHolder.courseId);

        Assertions.assertThat(request.getHeader().getRequestId()).isEqualTo(TestConstantHolder.requestId);
        Assertions.assertThat(request.getCourseId()).isEqualTo(TestConstantHolder.courseId);
    }

}
