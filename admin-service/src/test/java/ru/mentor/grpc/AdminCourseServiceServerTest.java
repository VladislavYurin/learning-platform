package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.mapper.TagMapper;
import ru.mentor.mapper.TagMapperImpl;
import ru.mentor.mapper.UtilMapperImpl;
import ru.mentor.facade.CourseFacade;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@SpringBootTest(classes = {
        AdminCourseServiceServer.class,
        CourseFacade.class,
        TagMapperImpl.class,
        UtilMapperImpl.class
})
class AdminCourseServiceServerTest {

    @MockBean
    private CourseFacade courseFacade;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private AdminCourseServiceServer adminCourseServiceServer;

    @Test
    void getCourse_successfulFlow_returnsCourseResponse() {
        final int TAGS_COUNT = 4;
        List<CourseTagEntity> listOfTags =
                TestEntityStubGenerator.constructCourseTagEntityList(TAGS_COUNT);
        List<CourseTagResponse> tags = listOfTags.stream().map(tagMapper::courseTagEntityToCourseTagResponse).toList();

        GetCourseRequest getCourseRequest = TestGrpcStubGenerator.constructGetCourseRequest();
        CourseResponse courseResponse = TestGrpcStubGenerator.constructCourseResponse();
        CourseResponse expectedCourseResponse =
                courseResponse.toBuilder()
                        .addAllTags(tags)
                        .build();

        Mockito.when(courseFacade.findCourseById(TestConstantHolder.COURSE_ID))
                .thenReturn(Mono.just(expectedCourseResponse));

        StepVerifier.create(adminCourseServiceServer.getCourse(Mono.just(getCourseRequest)))
                .expectNext(expectedCourseResponse)
                .verifyComplete();
    }

    @Test
    void getCourse_courseNotFound_returnsNotFoundStatus() {
        GetCourseRequest getCourseRequest = TestGrpcStubGenerator.constructGetCourseRequest();

        Mockito.when(courseFacade.findCourseById(TestConstantHolder.COURSE_ID))
                .thenReturn(Mono.error(
                        new EntityNotFoundException(TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT)));

        StepVerifier.create(adminCourseServiceServer.getCourse(Mono.just(getCourseRequest)))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(StatusRuntimeException.class, error);
                    StatusRuntimeException exception = (StatusRuntimeException) error;
                    Assertions.assertEquals(
                            exception.getStatus().getCode(),
                            Status.NOT_FOUND.getCode()
                    );

                    Assertions.assertEquals(
                            TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT,
                            exception.getStatus().getDescription()
                    );
                })
                .verify();
    }

    @Test
    void getCourse_emptyRequest_returnsInvalidArgumentStatus() {
        StepVerifier.create(adminCourseServiceServer.getCourse(Mono.empty()))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(StatusRuntimeException.class, error);

                    StatusRuntimeException statusRuntimeException =
                            (StatusRuntimeException) error;

                    Assertions.assertEquals(
                            statusRuntimeException.getStatus().getCode(),
                            Status.INVALID_ARGUMENT.getCode()
                    );

                    Assertions.assertEquals(
                            GrpcErrorText.EMPTY_REQUEST,
                            statusRuntimeException.getStatus().getDescription()
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
    void getAllCourses_successfulFlow_returnsResponse() {
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        AllCoursesResponse allCoursesResponse = TestGrpcStubGenerator.constructAllCoursesResponse();

        Mockito.when(courseFacade.findAllCourses(grpcPageRequest))
                .thenReturn(Mono.just(allCoursesResponse));

        StepVerifier.create(adminCourseServiceServer.getAllCourses(Mono.just(grpcPageRequest)))
                .expectNext(allCoursesResponse)
                .verifyComplete();
    }

    @Test
    void getAllCourses_entitiesNotFound_returnsNotFoundStatus() {
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();

        Mockito.when(courseFacade.findAllCourses(grpcPageRequest))
                .thenReturn(
                        Mono.error(new EntityNotFoundException(
                                TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT)));

        StepVerifier.create(adminCourseServiceServer.getAllCourses(Mono.just(grpcPageRequest)))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(StatusRuntimeException.class, error);
                    StatusRuntimeException statusRuntimeException = (StatusRuntimeException) error;
                    Assertions.assertEquals(
                            statusRuntimeException.getStatus().getCode(),
                            Status.NOT_FOUND.getCode()
                    );
                    Assertions.assertEquals(
                            TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT,
                            statusRuntimeException.getStatus().getDescription()
                    );
                })
                .verify();
    }

}