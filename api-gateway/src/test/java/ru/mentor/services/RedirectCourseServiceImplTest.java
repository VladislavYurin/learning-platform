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
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.front.CreateCourseRequest;
import ru.mentor.exception.GrpcRetryException;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.CourseServiceCourseGrpcClient;
import ru.mentor.mapper.CourseMapper;
import ru.mentor.services.impl.RedirectCourseServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;
import ru.mentor.utils.TestDataGenerator;

@ExtendWith(MockitoExtension.class)
class RedirectCourseServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private CourseServiceCourseGrpcClient courseGrpcClient;
    @Mock
    private CourseMapper courseMapper;
    @Mock
    private HeaderFactory headerFactory;

    @InjectMocks
    private RedirectCourseServiceImpl redirectCourseService;

    @Test
    void createCourse() {
        CreateCourseRequest createCourseRequest = TestDataGenerator.constructCreateCourseRequest();
        CreateCourseGrpcRequest grpcRequest = TestGrpcStubGenerator.constructCreateCourseGrpcRequest();
        CourseResponse grpcResponse = TestGrpcStubGenerator.constructCourseResponse();
        CourseDto dto = Mockito.mock(CourseDto.class);

        Mockito.when(courseMapper.constructGrpcCreateRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.userId),
                        ArgumentMatchers.eq(createCourseRequest)
                ))
                .thenReturn(grpcRequest);
        Mockito.when(courseMapper.mapGrpcCourseResponseToCourseDto(grpcResponse))
                .thenReturn(dto);
        Mockito.when(courseGrpcClient.createCourse(ArgumentMatchers.eq(grpcRequest)))
                .thenReturn(grpcResponse);
        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        CourseDto result = redirectCourseService.createCourse(createCourseRequest);

        Assertions.assertThat(result).isEqualTo(dto);
        Mockito.verify(courseMapper).constructGrpcCreateRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(createCourseRequest)
        );
        Mockito.verify(courseGrpcClient).createCourse(ArgumentMatchers.any(CreateCourseGrpcRequest.class));
        Mockito.verify(courseMapper).mapGrpcCourseResponseToCourseDto(grpcResponse);
    }

    @Test
    void deleteCourse() {
        DeleteCourseRequest deleteModuleRequest = TestGrpcStubGenerator.constructDeleteCourseRequest();

        Mockito.when(courseMapper.constructGrpcDeleteRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.userId),
                        ArgumentMatchers.eq(TestConstantHolder.courseId)
                ))
                .thenReturn(deleteModuleRequest);

        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        redirectCourseService.deleteCourse(TestConstantHolder.courseId);

        Mockito.verify(courseMapper).constructGrpcDeleteRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(TestConstantHolder.courseId)
        );
        Mockito.verify(courseGrpcClient).deleteCourse(ArgumentMatchers.eq(deleteModuleRequest));
    }

    @Test
    void getCourseById_success() {
        CourseResponse grpcResponse = TestGrpcStubGenerator.constructCourseResponse();
        CourseDto dto = Mockito.mock(CourseDto.class);

        Mockito.when(courseMapper.constructGrpcGetRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.userId),
                        ArgumentMatchers.eq(TestConstantHolder.courseId)
                ))
                .thenReturn(TestGrpcStubGenerator.constructGetCourseRequest());
        Mockito.when(courseGrpcClient.getCourse(ArgumentMatchers.any(GetCourseRequest.class)))
                .thenReturn(grpcResponse);
        Mockito.when(courseMapper.mapGrpcCourseResponseToCourseDto(grpcResponse))
                .thenReturn(dto);

        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        CourseDto result = redirectCourseService.getCourseById(TestConstantHolder.courseId);

        Assertions.assertThat(result).isEqualTo(dto);
        Mockito.verify(courseMapper).constructGrpcGetRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(TestConstantHolder.courseId)
        );
        Mockito.verify(courseGrpcClient).getCourse(ArgumentMatchers.any(GetCourseRequest.class));
        Mockito.verify(courseMapper).mapGrpcCourseResponseToCourseDto(grpcResponse);
    }

    @Test
    void getCourseById_failure() {
        GetCourseRequest getCourseRequest = TestGrpcStubGenerator.constructGetCourseRequest();
        Mockito.when(courseMapper.constructGrpcGetRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.userId),
                        ArgumentMatchers.eq(TestConstantHolder.courseId)
                ))
                .thenReturn(getCourseRequest);
        Mockito.when(courseGrpcClient.getCourse(ArgumentMatchers.eq(getCourseRequest)))
                .thenAnswer(invocation -> {
                    GetCourseRequest request = invocation.getArgument(0, GetCourseRequest.class);
                    throw new GrpcRetryException(TestConstantHolder.grpcExceptionText, request.getHeader().getRequestId());
                });

        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        Assertions.assertThatThrownBy(() -> redirectCourseService.getCourseById(TestConstantHolder.courseId))
                .isInstanceOf(GrpcRetryException.class)
                .hasMessageContaining(TestConstantHolder.grpcExceptionText);

        Mockito.verify(courseGrpcClient).getCourse(ArgumentMatchers.eq(getCourseRequest));
    }

    @Test
    void getAllCourses_success() {
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        AllCoursesResponse allCoursesResponse = TestGrpcStubGenerator.constructAllCoursesResponse();

        Page<CourseDto> courseDtoPage = TestEntityStubGenerator.constructCourseDtoPage();

        Mockito.when(courseMapper.constructGrpcPageRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.pageNumber),
                        ArgumentMatchers.eq(TestConstantHolder.pageSize),
                        ArgumentMatchers.eq(TestConstantHolder.userId)
                ))
                .thenReturn(grpcPageRequest);
        Mockito.when(courseGrpcClient.getAllCourses(grpcPageRequest))
                .thenReturn(allCoursesResponse);
        Mockito.when(courseMapper.mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse))
                .thenReturn(courseDtoPage);
        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        Page<CourseDto> result = redirectCourseService.getAllCourses(
                TestConstantHolder.pageNumber,
                TestConstantHolder.pageSize
        );

        Assertions.assertThat(result).isEqualTo(courseDtoPage);
        Mockito.verify(courseMapper).constructGrpcPageRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.pageNumber),
                ArgumentMatchers.eq(TestConstantHolder.pageSize),
                ArgumentMatchers.eq(TestConstantHolder.userId)
        );
        Mockito.verify(courseGrpcClient).getAllCourses(grpcPageRequest);
        Mockito.verify(courseMapper).mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse);
    }

    @Test
    void getAllCourses_failure() {
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();

        Mockito.when(courseMapper.constructGrpcPageRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.pageNumber),
                        ArgumentMatchers.eq(TestConstantHolder.pageSize),
                        ArgumentMatchers.eq(TestConstantHolder.userId)
                ))
                .thenReturn(grpcPageRequest);
        Mockito.when(courseGrpcClient.getAllCourses(grpcPageRequest))
                .thenAnswer(invocation -> {
                    GrpcPageRequest request = invocation.getArgument(0, GrpcPageRequest.class);
                    throw new GrpcRetryException(TestConstantHolder.grpcExceptionText, request.getHeader().getRequestId());
                });
        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        Assertions.assertThatThrownBy(() -> redirectCourseService.getAllCourses(
                        TestConstantHolder.pageNumber,
                        TestConstantHolder.pageSize
                ))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(TestConstantHolder.grpcExceptionText);

        Mockito.verify(courseMapper).constructGrpcPageRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.pageNumber),
                ArgumentMatchers.eq(TestConstantHolder.pageSize),
                ArgumentMatchers.eq(TestConstantHolder.userId)
        );
        Mockito.verify(courseGrpcClient).getAllCourses(grpcPageRequest);
    }

    @Test
    void getAllActiveCourses() {
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        AllCoursesResponse allCoursesResponse = TestGrpcStubGenerator.constructAllCoursesResponse();

        Page<CourseDto> courseDtoPage = TestEntityStubGenerator.constructCourseDtoPage();

        Mockito.when(courseMapper.constructGrpcPageRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.pageNumber),
                        ArgumentMatchers.eq(TestConstantHolder.pageSize),
                        ArgumentMatchers.eq(TestConstantHolder.userId)
                ))
                .thenReturn(grpcPageRequest);
        Mockito.when(courseGrpcClient.getAllActiveCourses(grpcPageRequest))
                .thenReturn(allCoursesResponse);
        Mockito.when(courseMapper.mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse))
                .thenReturn(courseDtoPage);
        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        Page<CourseDto> result = redirectCourseService.getAllActiveCourses(
                TestConstantHolder.pageNumber,
                TestConstantHolder.pageSize
        );

        Assertions.assertThat(result).isEqualTo(courseDtoPage);
        Mockito.verify(courseMapper).constructGrpcPageRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.pageNumber),
                ArgumentMatchers.eq(TestConstantHolder.pageSize),
                ArgumentMatchers.eq(TestConstantHolder.userId)
        );
        Mockito.verify(courseGrpcClient).getAllActiveCourses(grpcPageRequest);
        Mockito.verify(courseMapper).mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse);
    }
}