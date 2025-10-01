package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.GetCourseRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.UserMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class AdminCourseServiceServerTest {

    @Spy
    private UserMapper userMapper = new UserMapper();
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private BaseMapper baseMapper;
    @Spy
    private final AdminCourseMapper courseMapper = new AdminCourseMapper(userMapper);
    @InjectMocks
    private AdminCourseServiceServer adminCourseServiceServer;

    @Test
    void getCourse_successfulFlow_returnsCourseResponse() {
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        UserEntity authorEntity = TestEntityStubGenerator.constructAuthorUserEntity();

        GetCourseRequest getCourseRequest = TestGrpcStubGenerator.constructGetCourseRequest();
        CourseResponse expectedCourseResponse = TestGrpcStubGenerator.constructCourseResponse();

        Mockito.when(courseRepository.findByIdOrThrow(TestConstantHolder.COURSE_ID))
               .thenReturn(Mono.just(courseEntity));
        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.MENTOR_ID))
               .thenReturn(Mono.just(authorEntity));

        StepVerifier.create(adminCourseServiceServer.getCourse(Mono.just(getCourseRequest)))
                    .expectNext(expectedCourseResponse)
                    .verifyComplete();
    }

    @Test
    void getCourse_courseNotFound_returnsNotFoundStatus() {
        Mockito.when(courseRepository.findByIdOrThrow(TestConstantHolder.COURSE_ID))
               .thenReturn(Mono.error(
                       new EntityNotFoundException(TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT)));

        GetCourseRequest getCourseRequest = TestGrpcStubGenerator.constructGetCourseRequest();

        StepVerifier.create(adminCourseServiceServer.getCourse(Mono.just(getCourseRequest)))
                    .expectErrorSatisfies(error -> {

                        Assertions.assertInstanceOf(StatusRuntimeException.class, error);

                        StatusRuntimeException exception = (StatusRuntimeException) error;

                        Assertions.assertEquals(
                                exception.getStatus().getCode(),
                                Status.NOT_FOUND.getCode()
                        );

                        Assertions.assertEquals(
                                exception.getStatus().getDescription(),
                                TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT
                        );
                    })
                    .verify();
    }

    @Test
    void getCourse_courseAuthorNotFound_returnsNotFoundStatus() {
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();

        GetCourseRequest getCourseRequest = TestGrpcStubGenerator.constructGetCourseRequest();

        Mockito.when(courseRepository.findByIdOrThrow(TestConstantHolder.COURSE_ID))
               .thenReturn(Mono.just(courseEntity));

        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.COURSE_AUTHOR_ID))
               .thenReturn(Mono.error(
                       new EntityNotFoundException(TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT)));

        StepVerifier.create(adminCourseServiceServer.getCourse(getCourseRequest))
                    .expectErrorSatisfies(error -> {
                        Assertions.assertInstanceOf(StatusRuntimeException.class, error);

                        StatusRuntimeException statusRuntimeException =
                                (StatusRuntimeException) error;

                        Assertions.assertEquals(
                                statusRuntimeException.getStatus().getCode(),
                                Status.NOT_FOUND.getCode()
                        );

                        Assertions.assertEquals(
                                statusRuntimeException.getStatus().getDescription(),
                                TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT
                        );
                    })
                    .verify();
    }

    @Test
    void getCourse_emptyRequest_returnsInvalidArgumentStatus() {
        Mono<GetCourseRequest> getCourseRequest = Mono.empty();

        StepVerifier.create(adminCourseServiceServer.getCourse(getCourseRequest))
                    .expectErrorSatisfies(error -> {
                        Assertions.assertInstanceOf(StatusRuntimeException.class, error);

                        StatusRuntimeException statusRuntimeException =
                                (StatusRuntimeException) error;

                        Assertions.assertEquals(
                                statusRuntimeException.getStatus().getCode(),
                                Status.INVALID_ARGUMENT.getCode()
                        );

                        Assertions.assertEquals(
                                statusRuntimeException.getStatus().getDescription(),
                                GrpcErrorText.EMPTY_REQUEST
                        );
                    })
                    .verify();
    }

    @Test
    void getAllCourses_emptyRequest_returnsInvalidArgument() {
        StepVerifier.create(adminCourseServiceServer.getAllCourses(Mono.empty()))
                .expectErrorSatisfies(error -> {

                    Assertions.assertInstanceOf(StatusRuntimeException.class, error);
                    StatusRuntimeException statusRuntimeException = (StatusRuntimeException) error;

                    Assertions.assertEquals(
                            Status.Code.INVALID_ARGUMENT,
                            statusRuntimeException.getStatus().getCode()
                    );
                    Assertions.assertEquals(
                            TestConstantHolder.EMPTY_REQUEST_TEXT,
                            statusRuntimeException.getStatus().getDescription()
                    );
                })
                .verify();
    }

    @Test
    void getAllCourses_successfulFlow_returnsResponse(){
        CourseEntity courseEntityStub = TestEntityStubGenerator.constructCourseEntity();
        UserEntity userEntityStub = TestEntityStubGenerator.constructAuthorUserEntity();
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(
                TestConstantHolder.PAGE_NUMBER,
                TestConstantHolder.PAGE_SIZE
        );
        CourseResponse expectedCourseResponse = TestGrpcStubGenerator.constructCourseResponse();
        AllCoursesResponse allCoursesResponse = TestGrpcStubGenerator.constructAllCoursesResponse();

        Mockito.when(baseMapper.mapGrpcPageRequestToPageRequest(grpcPageRequest)).thenReturn(pageRequest);
        Mockito.when(courseRepository.findAllBy(pageRequest)).thenReturn(Flux.just(courseEntityStub));
        Mockito.when(userRepository.findById(TestConstantHolder.COURSE_AUTHOR_ID))
                .thenReturn(Mono.just(userEntityStub));

        Mockito.when(courseMapper.mapCourseEntityToGrpcCourseResponse(courseEntityStub, userEntityStub))
                        .thenReturn(expectedCourseResponse);
        Mockito.when(courseRepository.count()).thenReturn(Mono.just(TestConstantHolder.TOTAL_ELEMENTS_COUNT));

        StepVerifier.create(adminCourseServiceServer.getAllCourses(Mono.just(grpcPageRequest)))
                .expectNext(allCoursesResponse)
                .verifyComplete();

        Mockito.verify(baseMapper).mapGrpcPageRequestToPageRequest(grpcPageRequest);
        Mockito.verify(courseRepository).findAllBy(pageRequest);
        Mockito.verify(userRepository).findById(userEntityStub.getId());
        Mockito.verify(courseRepository).count();
        Mockito.verify(courseMapper).mapCourseEntityToGrpcCourseResponse(courseEntityStub, userEntityStub);
        Mockito.verify(courseMapper, Mockito.atLeastOnce())
                .mapCourseResponsePageToGrpcAllCoursesResponse(ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(baseMapper, courseRepository, userRepository, courseMapper);
    }

    @Test
    void getAllCourses_entitiesNotFound_returnsNotFoundStatus(){
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(
                TestConstantHolder.PAGE_NUMBER,
                TestConstantHolder.PAGE_SIZE
        );

        Mockito.when(courseRepository.findAllBy(pageRequest)).thenReturn(Flux.error(new EntityNotFoundException(
                TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT)));

        Mockito.when(courseRepository.count()).thenReturn(Mono.just(0L));

        StepVerifier.create(adminCourseServiceServer.getAllCourses(Mono.just(grpcPageRequest)))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(StatusRuntimeException.class, error);
                    StatusRuntimeException statusRuntimeException = (StatusRuntimeException) error;

                    Assertions.assertEquals(
                            statusRuntimeException.getStatus().getCode(),
                            Status.NOT_FOUND.getCode()
                    );
                    Assertions.assertEquals(
                            statusRuntimeException.getStatus().getDescription(),
                            TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT
                    );
                })
                .verify();
    }
}