package ru.mentor.grpc;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.entity.CourseEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.UserMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class AdminCourseServiceServerTest {

    private final UserMapper userMapper = new UserMapper();

    @Mock
    private CourseRepository courseRepository;
    @Spy
    private BaseMapper baseMapper;
    @Spy
    private final AdminCourseMapper courseMapper = new AdminCourseMapper(userMapper);
    @Mock
    private StreamObserver<CourseResponse> courseResponseObserver;
    @Mock
    private StreamObserver<AllCoursesResponse> allCoursesResponseObserver;
    @Captor
    private ArgumentCaptor<CourseResponse> courseResponseCaptor;
    @Captor
    private ArgumentCaptor<AllCoursesResponse> allCoursesResponseCaptor;
    @Captor
    private ArgumentCaptor<Throwable> entityNotFoundCaptor;
    @InjectMocks
    private AdminCourseServiceServer adminCourseServiceServer;

    @Test
    void getCourse_success() {
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        CourseResponse courseResponse = TestGrpcStubGenerator.constructCourseResponse();

        Mockito.when(courseRepository.findByIdOrThrow(TestConstantHolder.courseId))
               .thenReturn(courseEntity);

        adminCourseServiceServer.getCourse(
                TestGrpcStubGenerator.constructGetCourseRequest(),
                courseResponseObserver
        );

        Mockito.verify(courseResponseObserver).onNext(courseResponseCaptor.capture());
        Assertions.assertThat(courseResponseCaptor.getValue())
                  .usingRecursiveComparison()
                  .isEqualTo(courseResponse);
    }

    @Test
    void getCourse_notFound() {
        Mockito.when(courseRepository.findByIdOrThrow(TestConstantHolder.courseId))
               .thenThrow(new EntityNotFoundException(TestConstantHolder.notFoundExceptionText));

        adminCourseServiceServer.getCourse(
                TestGrpcStubGenerator.constructGetCourseRequest(),
                courseResponseObserver
        );

        Mockito.verify(courseResponseObserver).onError(entityNotFoundCaptor.capture());

        Throwable entityNotFoundException = entityNotFoundCaptor.getValue();
        Assertions.assertThat(entityNotFoundException)
                  .isInstanceOf(StatusRuntimeException.class)
                  .hasMessageContaining(TestConstantHolder.notFoundExceptionText);
    }

    @Test
    void getAllCourses_success() {
        GrpcPageRequest grpcRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(
                TestConstantHolder.pageNumber,
                TestConstantHolder.pageSize
        );

        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        Page<CourseEntity> coursePage = new PageImpl<>(
                List.of(courseEntity));

        AllCoursesResponse allCoursesResponse = TestGrpcStubGenerator.constructAllCoursesResponse();

        Mockito.when(courseRepository.findAll(pageRequest))
               .thenReturn(coursePage);

        adminCourseServiceServer.getAllCourses(grpcRequest, allCoursesResponseObserver);

        Mockito.verify(allCoursesResponseObserver).onNext(allCoursesResponseCaptor.capture());
        Assertions.assertThat(allCoursesResponseCaptor.getValue())
                  .usingRecursiveComparison().isEqualTo(allCoursesResponse);
    }

    @Test
    void getAllCourses_notFound() {
        GrpcPageRequest grpcRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(
                TestConstantHolder.pageNumber,
                TestConstantHolder.pageSize
        );

        Mockito.when(courseRepository.findAll(pageRequest))
               .thenThrow(new EntityNotFoundException(TestConstantHolder.notFoundExceptionText));

        adminCourseServiceServer.getAllCourses(grpcRequest, allCoursesResponseObserver);

        Mockito.verify(allCoursesResponseObserver).onError(entityNotFoundCaptor.capture());

        Throwable entityNotFoundException = entityNotFoundCaptor.getValue();
        Assertions.assertThat(entityNotFoundException)
                  .isInstanceOf(StatusRuntimeException.class)
                  .hasMessageContaining(TestConstantHolder.notFoundExceptionText);
    }

}