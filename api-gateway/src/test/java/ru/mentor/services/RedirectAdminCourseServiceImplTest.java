package ru.mentor.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.gateway.model.CourseDto;
import ru.mentor.grpc.AdminCourseServiceGrpcClient;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.CourseDtoMapper;
import ru.mentor.services.impl.RedirectAdminCourseServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class RedirectAdminCourseServiceImplTest {

    @Mock
    private AdminCourseServiceGrpcClient courseServiceClient;
    @Mock
    private AdminCourseMapper courseMapper;
    @Mock
    private BaseMapper baseMapper;
    @Mock
    private CourseDtoMapper courseDtoMapper;

    @InjectMocks
    private RedirectAdminCourseServiceImpl redirectService;

    @Test
    void getCourseById_success() {
        CourseResponse grpcResponse = TestGrpcStubGenerator.constructCourseResponse();
        CourseDto dto = Mockito.mock(CourseDto.class);

        Mockito.when(courseMapper.constructGetCourseRequest(
                       ArgumentMatchers.anyString(),
                       ArgumentMatchers.eq(TestConstantHolder.courseId)
               ))
               .thenReturn(TestGrpcStubGenerator.constructGetCourseRequest());
        Mockito.when(courseServiceClient.getCourse(ArgumentMatchers.any(GetCourseRequest.class)))
               .thenReturn(grpcResponse);
        Mockito.when(courseMapper.mapGrpcCourseResponseToCourseDto(grpcResponse))
               .thenReturn(dto);

        CourseDto result = redirectService.getCourseById(TestConstantHolder.courseId);

        Assertions.assertThat(result).isEqualTo(dto);
        Mockito.verify(courseMapper).constructGetCourseRequest(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(TestConstantHolder.courseId)
        );
        Mockito.verify(courseServiceClient).getCourse(ArgumentMatchers.any(GetCourseRequest.class));
        Mockito.verify(courseMapper).mapGrpcCourseResponseToCourseDto(grpcResponse);
    }

    @Test
    void getCourseById_failure() {
        Mockito.when(courseMapper.constructGetCourseRequest(
                       ArgumentMatchers.anyString(),
                       ArgumentMatchers.eq(TestConstantHolder.courseId)
               ))
               .thenReturn(TestGrpcStubGenerator.constructGetCourseRequest());
        Mockito.when(courseServiceClient.getCourse(ArgumentMatchers.any(GetCourseRequest.class)))
               .thenThrow(new RuntimeException(TestConstantHolder.notFoundExceptionText));

        Assertions.assertThatThrownBy(() -> redirectService.getCourseById(TestConstantHolder.courseId))
                  .isInstanceOf(RuntimeException.class)
                  .hasMessageContaining(TestConstantHolder.notFoundExceptionText);

        Mockito.verify(courseMapper).constructGetCourseRequest(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(TestConstantHolder.courseId)
        );
        Mockito.verify(courseServiceClient).getCourse(ArgumentMatchers.any(GetCourseRequest.class));
    }

    @Test
    void getAllCourses_success() {
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        AllCoursesResponse allCoursesResponse = TestGrpcStubGenerator.constructAllCoursesResponse();

        Page<ru.mentor.dto.CourseDto> courseDtoPage = TestEntityStubGenerator.constructCourseDtoPage();

        Mockito.when(baseMapper.constructGrpcPageRequest(
                       ArgumentMatchers.anyString(),
                       ArgumentMatchers.anyInt(),
                       ArgumentMatchers.anyInt()
               ))
               .thenReturn(grpcPageRequest);
        Mockito.when(courseServiceClient.getAllCourses(grpcPageRequest))
               .thenReturn(allCoursesResponse);
        Mockito.when(courseMapper.mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse))
               .thenReturn(courseDtoMapper.toApiPage(courseDtoPage));

        Page<CourseDto> result = redirectService.getAllCourses(
                TestConstantHolder.pageNumber,
                TestConstantHolder.pageSize
        );

        Assertions.assertThat(result).isEqualTo(courseDtoPage);
        Mockito.verify(baseMapper).constructGrpcPageRequest(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
        );
        Mockito.verify(courseServiceClient).getAllCourses(grpcPageRequest);
        Mockito.verify(courseMapper).mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse);
    }

    @Test
    void getAllCourses_failure() {

        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();

        Mockito.when(baseMapper.constructGrpcPageRequest(
                       ArgumentMatchers.anyString(),
                       ArgumentMatchers.anyInt(),
                       ArgumentMatchers.anyInt()
               ))
               .thenReturn(grpcPageRequest);
        Mockito.when(courseServiceClient.getAllCourses(grpcPageRequest))
               .thenThrow(new RuntimeException(TestConstantHolder.notFoundExceptionText));

        Assertions.assertThatThrownBy(() -> redirectService.getAllCourses(
                          TestConstantHolder.pageNumber,
                          TestConstantHolder.pageSize
                  ))
                  .isInstanceOf(RuntimeException.class)
                  .hasMessageContaining(TestConstantHolder.notFoundExceptionText);

        Mockito.verify(baseMapper).constructGrpcPageRequest(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
        );
        Mockito.verify(courseServiceClient).getAllCourses(grpcPageRequest);
    }

}