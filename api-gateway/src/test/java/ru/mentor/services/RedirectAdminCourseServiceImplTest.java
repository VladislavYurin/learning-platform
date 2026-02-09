package ru.mentor.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.dto.CourseDto;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.AdminCourseServiceGrpcClient;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.AdminCourseMapperImpl;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.BaseMapperImpl;
import ru.mentor.services.impl.RedirectAdminCourseServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@SpringBootTest(classes = {
        RedirectAdminCourseServiceImpl.class,
        BaseMapperImpl.class,
        AdminCourseMapperImpl.class
})
class RedirectAdminCourseServiceImplTest {

    @MockBean
    private AdminCourseServiceGrpcClient courseServiceClient;

    @MockBean
    private AdminCourseMapper courseMapper;

    @MockBean
    private BaseMapper baseMapper;

    @MockBean
    private HeaderFactory headerFactory;

    @Autowired
    private RedirectAdminCourseServiceImpl redirectService;

    @BeforeEach
    void setUp() {
        Mockito.when(headerFactory.create(ArgumentMatchers.anyString()))
                .thenReturn(TestGrpcStubGenerator.constructHeader());
    }

    @Test
    void getCourseById_success() {
        CourseResponse grpcResponse = TestGrpcStubGenerator.constructCourseResponse();
        CourseDto dto = Mockito.mock(CourseDto.class);

        Mockito.when(courseMapper.toGetCourseRequest(
                        ArgumentMatchers.any(Header.class),
                        ArgumentMatchers.eq(TestConstantHolder.courseId)
                ))
                .thenReturn(TestGrpcStubGenerator.constructGetCourseRequest());
        Mockito.when(courseServiceClient.getCourse(ArgumentMatchers.any(GetCourseRequest.class)))
                .thenReturn(grpcResponse);
        Mockito.when(courseMapper.courseResponseToCourseDto(grpcResponse))
                .thenReturn(dto);

        CourseDto result = redirectService.getCourseById(TestConstantHolder.courseId);

        Assertions.assertThat(result).isEqualTo(dto);
        Mockito.verify(courseMapper).toGetCourseRequest(
                ArgumentMatchers.any(Header.class),
                ArgumentMatchers.eq(TestConstantHolder.courseId)
        );
        Mockito.verify(courseServiceClient).getCourse(ArgumentMatchers.any(GetCourseRequest.class));
        Mockito.verify(courseMapper).courseResponseToCourseDto(grpcResponse);
    }

    @Test
    void getCourseById_failure() {
        Mockito.when(courseMapper.toGetCourseRequest(
                        ArgumentMatchers.any(Header.class),
                        ArgumentMatchers.eq(TestConstantHolder.courseId)
                ))
                .thenReturn(TestGrpcStubGenerator.constructGetCourseRequest());
        Mockito.when(courseServiceClient.getCourse(ArgumentMatchers.any(GetCourseRequest.class)))
                .thenThrow(new RuntimeException(TestConstantHolder.notFoundExceptionText));

        Assertions.assertThatThrownBy(() -> redirectService.getCourseById(TestConstantHolder.courseId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(TestConstantHolder.notFoundExceptionText);

        Mockito.verify(courseMapper).toGetCourseRequest(
                ArgumentMatchers.any(Header.class),
                ArgumentMatchers.eq(TestConstantHolder.courseId)
        );
        Mockito.verify(courseServiceClient).getCourse(ArgumentMatchers.any(GetCourseRequest.class));
    }

    @Test
    void getAllCourses_success() {
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        AllCoursesResponse allCoursesResponse = TestGrpcStubGenerator.constructAllCoursesResponse();

        Page<CourseDto> courseDtoPage = TestEntityStubGenerator.constructCourseDtoPage();

        Mockito.when(baseMapper.toGrpcPageRequest(
                        ArgumentMatchers.any(Header.class),
                        ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt()
                ))
                .thenReturn(grpcPageRequest);
        Mockito.when(courseServiceClient.getAllCourses(grpcPageRequest))
                .thenReturn(allCoursesResponse);
        Mockito.when(courseMapper.allCoursesResponseToCourseDtoPage(allCoursesResponse))
                .thenReturn(courseDtoPage);

        Page<CourseDto> result = redirectService.getAllCourses(
                TestConstantHolder.zero,
                TestConstantHolder.pageSize
        );

        Assertions.assertThat(result).isEqualTo(courseDtoPage);
        Mockito.verify(baseMapper).toGrpcPageRequest(
                ArgumentMatchers.any(Header.class),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
        );
        Mockito.verify(courseServiceClient).getAllCourses(grpcPageRequest);
        Mockito.verify(courseMapper).allCoursesResponseToCourseDtoPage(allCoursesResponse);
    }

    @Test
    void getAllCourses_failure() {

        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();

        Mockito.when(baseMapper.toGrpcPageRequest(
                        ArgumentMatchers.any(Header.class),
                        ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt()
                ))
                .thenReturn(grpcPageRequest);
        Mockito.when(courseServiceClient.getAllCourses(grpcPageRequest))
                .thenThrow(new RuntimeException(TestConstantHolder.notFoundExceptionText));

        Assertions.assertThatThrownBy(() -> redirectService.getAllCourses(
                        TestConstantHolder.zero,
                        TestConstantHolder.pageSize
                ))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(TestConstantHolder.notFoundExceptionText);

        Mockito.verify(baseMapper).toGrpcPageRequest(
                ArgumentMatchers.any(Header.class),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
        );
        Mockito.verify(courseServiceClient).getAllCourses(grpcPageRequest);
    }

}