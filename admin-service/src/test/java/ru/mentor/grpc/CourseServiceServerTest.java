package ru.mentor.grpc;

import static org.assertj.core.api.Assertions.assertThat;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.GetCourseRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.entity.CourseEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;

@ExtendWith(MockitoExtension.class)
class CourseServiceServerTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private AdminCourseMapper courseMapper;

    @Mock
    private BaseMapper baseMapper;

    @Mock
    private StreamObserver<CourseResponse> courseResponseObserver;

    @Mock
    private StreamObserver<AllCoursesResponse> allCoursesResponseObserver;

    @InjectMocks
    private CourseServiceServer courseServiceServer;

    @Captor
    private ArgumentCaptor<CourseResponse> courseResponseCaptor;

    @Captor
    private ArgumentCaptor<AllCoursesResponse> allCoursesResponseCaptor;

    private final String requestId = UUID.randomUUID().toString();
    private final int pageNumber = 0;
    private final int pageSize = 10;

    private final long courseEntityId = 1L;

    private final String notFoundExceptionText = "not found";

    private GrpcPageRequest constructGrpcPageRequest() {
        return GrpcPageRequest.newBuilder()
                              .setRequestId(requestId)
                              .setPageNumber(pageNumber)
                              .setPageSize(pageSize)
                              .build();
    }

    private GetCourseRequest constructGetCourseRequest() {
        return GetCourseRequest.newBuilder()
                               .setRequestId(requestId)
                               .setCourseId(courseEntityId)
                               .build();
    }

    private CourseEntity constructCourseEntity() {
        return CourseEntity.builder()
                           .id(courseEntityId)
                           .build();
    }

    private CourseResponse constructCourseResponse() {
        return CourseResponse.newBuilder()
                             .setCourseId(courseEntityId)
                             .build();
    }

    private AllCoursesResponse constructAllCoursesResponse() {
        return AllCoursesResponse.newBuilder().addCourses(
                constructCourseResponse()).build();
    }

    @Test
    void getCourse_success() {
        CourseEntity courseEntity = constructCourseEntity();
        CourseResponse courseResponse = constructCourseResponse();

        Mockito.when(courseRepository.findByIdOrThrow(courseEntityId))
               .thenReturn(courseEntity);
        Mockito.when(courseMapper.mapCourseEntityToGrpcCourseResponse(courseEntity))
               .thenReturn(courseResponse);

        courseServiceServer.getCourse(constructGetCourseRequest(), courseResponseObserver);

        Mockito.verify(courseResponseObserver).onNext(courseResponseCaptor.capture());
        assertThat(courseResponseCaptor.getValue().getCourseId()).isEqualTo(courseEntityId);
    }

    @Test
    void getCourse_notFound() {
        Mockito.when(courseRepository.findByIdOrThrow(1L))
               .thenThrow(new EntityNotFoundException(notFoundExceptionText));

        courseServiceServer.getCourse(constructGetCourseRequest(), courseResponseObserver);

        Mockito.verify(courseResponseObserver).onError(
                ArgumentMatchers.argThat(throwable ->
                                                 throwable instanceof StatusRuntimeException &&
                                                         ((StatusRuntimeException) throwable).getStatus()
                                                                                             .getDescription()
                                                                                             .contains(
                                                                                                     notFoundExceptionText)
                )
        );
    }

    @Test
    void getAllCourses_success() {
        GrpcPageRequest grpcRequest = constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        CourseEntity courseEntity = constructCourseEntity();
        Page<CourseEntity> coursePage = new PageImpl<>(
                List.of(courseEntity));

        AllCoursesResponse allCoursesResponse = constructAllCoursesResponse();

        Mockito.when(baseMapper.mapGrpcPageRequestToPageRequest(grpcRequest))
               .thenReturn(pageRequest);
        Mockito.when(courseRepository.findAll(pageRequest))
               .thenReturn(coursePage);
        Mockito.when(courseMapper.mapCourseEntityPageToGrpcAllCoursesResponse(coursePage))
               .thenReturn(allCoursesResponse);

        courseServiceServer.getAllCourses(grpcRequest, allCoursesResponseObserver);

        Mockito.verify(allCoursesResponseObserver).onNext(allCoursesResponseCaptor.capture());
        assertThat(allCoursesResponseCaptor.getValue().getCoursesCount()).isEqualTo(1);
    }

    @Test
    void getAllCourses_notFound() {
        GrpcPageRequest grpcRequest = constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Mockito.when(baseMapper.mapGrpcPageRequestToPageRequest(grpcRequest))
               .thenReturn(pageRequest);
        Mockito.when(courseRepository.findAll(pageRequest))
               .thenThrow(new EntityNotFoundException(notFoundExceptionText));

        courseServiceServer.getAllCourses(grpcRequest, allCoursesResponseObserver);

        Mockito.verify(allCoursesResponseObserver).onError(
                ArgumentMatchers.argThat(throwable ->
                                                 throwable instanceof StatusRuntimeException &&
                                                         ((StatusRuntimeException) throwable).getStatus()
                                                                                             .getDescription()
                                                                                             .contains(
                                                                                                     notFoundExceptionText)
                )
        );
    }

}