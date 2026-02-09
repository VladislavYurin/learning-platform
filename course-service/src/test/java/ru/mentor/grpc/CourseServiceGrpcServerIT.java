package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;
import ru.mentor.CourseApplication;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.DeleteCourseResponse;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.ReactorCourseServiceGrpc;
import ru.mentor.common.ReactorModuleServiceGrpc;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.CourseTagLinkRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@Slf4j
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(classes = CourseApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CourseServiceGrpcServerIT {

    public static String POSTGRES_IMAGE_VERSION = "postgres:15-alpine";

    public final Long COURSE_ID = TestConstantHolder.COURSE_ID;
    public final long NEW_COURSE_ID = COURSE_ID + 1;
    private final long AUTHOR_ID = 1L;

    private final long INVALID_AUTHOR_ID = 4L;
    private final long INVALID_COURSE_ID = 111L;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE_VERSION)
            .withInitScript("schema.sql");

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseTagLinkRepository courseTagLinkRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @GrpcClient("inProcess")
    private ReactorCourseServiceGrpc.ReactorCourseServiceStub courseServiceStub;

    @GrpcClient("inProcess")
    private ReactorModuleServiceGrpc.ReactorModuleServiceStub reactorModuleServiceStub;

    @Test
    @Order(1)
    void getCourse_validCourseId_shouldReturnCourse() {
        GetCourseRequest request = TestGrpcStubGenerator.constructGetCourseRequest();
        request = request.toBuilder().setSenderId(AUTHOR_ID).build();
        StepVerifier.create(courseServiceStub.getCourse(request))
                    .assertNext(response -> {
                        Assertions.assertEquals(
                                TestConstantHolder.COURSE_TITLE,
                                response.getTitle()
                        );
                        Assertions.assertEquals(
                                TestConstantHolder.COURSE_DESCRIPTION,
                                response.getDescription()
                        );
                        Assertions.assertEquals(
                                TestConstantHolder.USERNAME,
                                response.getAuthor().getUsername()
                        );
                        Assertions.assertTrue(response.getIsActive());
                        Assertions.assertEquals(1, response.getModulesList().size());
                        Assertions.assertEquals(2, response.getTagsList().size());
                    })
                    .verifyComplete();
    }

    @Test
    @Order(2)
    void getCourse_invalidCourseId_shouldThrowNotFoundException() {
        GetCourseRequest request = TestGrpcStubGenerator.constructGetCourseRequest();
        request = request.toBuilder()
                         .setCourseId(INVALID_COURSE_ID)
                         .setSenderId(AUTHOR_ID)
                         .build();

        StepVerifier.create(courseServiceStub.getCourse(request))
                    .expectErrorSatisfies(ex -> {
                        Assertions.assertInstanceOf(StatusRuntimeException.class, ex);
                        StatusRuntimeException statusException = (StatusRuntimeException) ex;
                        Assertions.assertEquals(
                                Status.NOT_FOUND.getCode(),
                                statusException.getStatus().getCode()
                        );
                    })
                    .verify();
    }

    @Test
    @Order(3)
    void getAllCourses_validData_shouldReturnAllCoursesResponse() {
        GrpcPageRequest pageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        pageRequest = pageRequest.toBuilder().setSenderId(AUTHOR_ID).build();

        StepVerifier.create(courseServiceStub.getAllCourses(pageRequest))
                    .assertNext(response -> {
                        Assertions.assertEquals(1, response.getCoursesCount());
                        CourseResponse course = response.getCoursesList().get(0);
                        Assertions.assertEquals(TestConstantHolder.COURSE_TITLE, course.getTitle());
                        Assertions.assertEquals(
                                TestConstantHolder.COURSE_DESCRIPTION,
                                course.getDescription()
                        );
                        Assertions.assertEquals(
                                TestConstantHolder.USERNAME,
                                course.getAuthor().getUsername()
                        );
                        Assertions.assertTrue(course.getIsActive());
                        Assertions.assertEquals(1, course.getModulesList().size());
                        Assertions.assertEquals(2, course.getTagsList().size());

                        Assertions.assertEquals(
                                TestConstantHolder.PAGE_NUMBER,
                                response.getPageDetails().getPage()
                        );
                        Assertions.assertEquals(
                                TestConstantHolder.PAGE_SIZE,
                                response.getPageDetails().getSize()
                        );
                        Assertions.assertEquals(
                                TestConstantHolder.TOTAL_ELEMENTS_COUNT,
                                response.getPageDetails().getTotalElements()
                        );
                        Assertions.assertEquals(
                                TestConstantHolder.TOTAL_PAGES_COUNT,
                                response.getPageDetails().getTotalPages()
                        );
                    })
                    .verifyComplete();
    }

    @Test
    @Order(4)
    void createCourse_validData_shouldCreateCourse() {
        CreateCourseGrpcRequest createCourseRequest = TestGrpcStubGenerator.constructCreateCourseRequest();
        createCourseRequest = createCourseRequest.toBuilder().setUserId(AUTHOR_ID).build();

        StepVerifier.create(courseServiceStub.createCourse(createCourseRequest))
                    .assertNext(response -> {
                        Assertions.assertEquals(AUTHOR_ID, response.getAuthor().getUserId());
                        Assertions.assertEquals(TestConstantHolder.COURSE_TITLE, response.getTitle());
                        Assertions.assertEquals(TestConstantHolder.COURSE_DESCRIPTION, response.getDescription());
                        Assertions.assertTrue(response.getIsActive());
                    }).verifyComplete();

        StepVerifier.create(courseRepository.findById(NEW_COURSE_ID))
                    .assertNext(courseEntity -> {
                        Assertions.assertEquals(TestConstantHolder.COURSE_TITLE, courseEntity.getCourseTitle());
                        Assertions.assertEquals(AUTHOR_ID, courseEntity.getAuthorId());
                        Assertions.assertEquals(
                                TestConstantHolder.COURSE_DESCRIPTION,
                                courseEntity.getDescription()
                        );
                        Assertions.assertTrue(courseEntity.getIsActive());
                    }).verifyComplete();
    }

    @Test
    @Order(5)
    void createCourse_invalidUserId_shouldThrowNotFoundException() {
        CreateCourseGrpcRequest request = TestGrpcStubGenerator.constructCreateCourseRequest();
        request = request.toBuilder().setUserId(INVALID_AUTHOR_ID).build();

        StepVerifier.create(courseServiceStub.createCourse(request))
                    .expectErrorSatisfies(ex -> {
                        Assertions.assertInstanceOf(StatusRuntimeException.class, ex);
                        StatusRuntimeException statusEx = (StatusRuntimeException) ex;
                        Assertions.assertEquals(
                                Status.NOT_FOUND.getCode(),
                                statusEx.getStatus().getCode()
                        );
                    })
                    .verify();

    }

    @Test
    @Order(6)
    void deleteCourse_validCourse_shouldRemoveCourse() {
        DeleteCourseRequest request = TestGrpcStubGenerator.constructDeleteCourseRequest();
        request = request.toBuilder()
                         .setSenderId(AUTHOR_ID)
                         .setCourseId(NEW_COURSE_ID)
                         .build();

        StepVerifier.create(courseServiceStub.deleteCourse(request))
                    .assertNext(response ->
                                        Assertions.assertInstanceOf(
                                                DeleteCourseResponse.class,
                                                response
                                        ))
                    .verifyComplete();

        StepVerifier.create(courseRepository.findByIdOrThrow(NEW_COURSE_ID))
                    .expectErrorSatisfies(ex -> Assertions.assertInstanceOf(
                            EntityNotFoundException.class, ex))
                    .verify();

        StepVerifier.create(courseTagLinkRepository.findByIdCourse(NEW_COURSE_ID).collectList())
                    .assertNext(links ->
                                    Assertions.assertTrue(links.isEmpty()))
                    .verifyComplete();
    }

    @Test
    @Order(7)
    void getModule_mentorRequestAccessPermitted_shouldReturnModule() {
        GetModuleRequest request = TestGrpcStubGenerator.constructGetModuleRequest();
        request = request.toBuilder().setSenderId(AUTHOR_ID).build();

        StepVerifier.create(reactorModuleServiceStub.getModule(request))
                .assertNext(response -> {
                    Assertions.assertEquals(TestConstantHolder.MODULE_TITLE, response.getTitle());
                    Assertions.assertEquals(COURSE_ID, response.getCourseId());
                    Assertions.assertEquals(TestConstantHolder.MODULE_CONTENT, response.getContent());
                    Assertions.assertEquals(TestConstantHolder.MODULE_ORDER_NUMBER, response.getOrderNumber());
                    Assertions.assertTrue(response.getIsActive());
                })
                .verifyComplete();
    }

    @Test
    @Order(8)
    void getModule_mentorRequestAccessDenied_shouldThrowException() {
        UserEntity notAuthor = TestEntityStubGenerator.constructMentorUserEntity();
        notAuthor.setId(null);
        notAuthor = userRepository.save(notAuthor).block();

        GetModuleRequest request = TestGrpcStubGenerator.constructGetModuleRequest();
        request = request.toBuilder().setSenderId(notAuthor.getId()).build();

        StepVerifier.create(reactorModuleServiceStub.getModule(request))
                    .expectErrorSatisfies(ex -> {
                        Assertions.assertInstanceOf(StatusRuntimeException.class, ex);
                        StatusRuntimeException statusException = (StatusRuntimeException) ex;
                        Assertions.assertEquals(
                                Status.PERMISSION_DENIED.getCode(),
                                statusException.getStatus().getCode()
                        );
                    })
                    .verify();
    }
}