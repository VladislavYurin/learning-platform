package ru.mentor.grpc;

import static ru.mentor.testUtil.TestConstantHolder.MENTOR_TAG_NAME_BADGE;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructListMentorTagEntity;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorUserEntityWithoutId;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructRegularUserEntityWithoutId;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructTestMentorTagEntityWithoutId;
import static ru.mentor.testUtil.TestGrpcStubGenerator.constructAllMentorTagsRequest;
import static ru.mentor.testUtil.TestGrpcStubGenerator.constructAttachMentorTagsRequest;
import static ru.mentor.testUtil.TestGrpcStubGenerator.constructCreateCustomMentorTagRequest;
import static ru.mentor.testUtil.TestGrpcStubGenerator.constructDetachMentorTagRequest;

import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.util.List;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.admin.ReactorAdminMentorServiceGrpc;
import ru.mentor.entity.MentorTagEntity;
import ru.mentor.entity.MentorTagLinkEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.grpc.tags.AllMentorTagsRequset;
import ru.mentor.grpc.tags.AllMentorTagsResponse;
import ru.mentor.grpc.tags.AttachMentorTagsRequest;
import ru.mentor.grpc.tags.AttachMentorTagsResponse;
import ru.mentor.grpc.tags.CreateCustomMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagResponse;
import ru.mentor.grpc.tags.MentorTagResponse;
import ru.mentor.repository.MentorTagLinkRepository;
import ru.mentor.repository.MentorTagRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@SpringBootTest(webEnvironment = WebEnvironment.NONE,
        properties = {
                "grpc.server.inProcessName=test",
                "grpc.server.port=-1",
                "grpc.client.inProcess.address=in-process:test",
                "grpc.client.inProcess.negotiation-type=plaintext"
        })
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdminMentorServiceServerTest {

    @Container
    static PostgreSQLContainer postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:15")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("psw")
            .withInitScript("testInit.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                "r2dbc:postgresql://"
                        + postgres.getHost() + ":"
                        + postgres.getMappedPort(5432)
                        + "/" + postgres.getDatabaseName()
        );
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);


        registry.add("spring.liquibase.enabled", () -> "false");

        registry.add("grpc.server.inProcessName", () -> "test");
        registry.add("grpc.server.port", () -> "-1");
        registry.add("grpc.client.inProcess.address", () -> "in-process:test");
        registry.add("grpc.client.inProcess.negotiation-type", () -> "plaintext");
        registry.add("grpc.common.api-key", () -> "test-api-key");
        registry.add("grpc.common.node-id", () -> "test-node-id");
    }

    @GrpcClient("inProcess")
    ReactorAdminMentorServiceGrpc.ReactorAdminMentorServiceStub stub;

    @Autowired
    private MentorTagRepository mentorTagRepository;

    @Autowired
    private MentorTagLinkRepository mentorTagLinkRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        mentorTagLinkRepository.deleteAll().block();
        mentorTagRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    @Test
    void smokeTest() {
        Assertions.assertTrue(postgres.isRunning());

        MentorTagEntity tag = mentorTagRepository.save(constructTestMentorTagEntityWithoutId()
        ).block();

        Assertions.assertNotNull(tag);
        Assertions.assertNotNull(tag.getId());
        Assertions.assertEquals(TestConstantHolder.MENTOR_TAG_NAME_DIRECTION, tag.getTagName());

        Long count = mentorTagRepository.count().block();
        Assertions.assertTrue(count > 0);

        AllMentorTagsRequset request = TestGrpcStubGenerator.constructAllMentorTagsRequest();

        AllMentorTagsResponse response = stub.listMentorTags(Mono.just(request))
                                             .block();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getAllMentorsTagsList().size());
        Assertions.assertEquals(TestConstantHolder.MENTOR_TAG_NAME_DIRECTION, response.getAllMentorsTagsList().get(0).getName());
    }

    @Test
    void createCustomMentorTag_success_savedToDatabase() {
        CreateCustomMentorTagRequest request = constructCreateCustomMentorTagRequest();

        MentorTagResponse response = stub.createCustomMentorTag(Mono.just(request))
                                         .block();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(MENTOR_TAG_NAME_BADGE, response.getMentorTag().getName());

        StepVerifier.create(mentorTagRepository.findAll())
                    .assertNext(tag -> {
                        Assertions.assertEquals(MENTOR_TAG_NAME_BADGE, tag.getTagName());
                        Assertions.assertNotNull(tag.getId());
                    })
                    .verifyComplete();
    }

    @Test
    void createCustomMentorTag_whenTagAlreadyExists_thenAlreadyExistsStatus() {

        CreateCustomMentorTagRequest request = constructCreateCustomMentorTagRequest();

        stub.createCustomMentorTag(Mono.just(request)).block();

        StatusRuntimeException exception = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> stub.createCustomMentorTag(Mono.just(request)).block()
        );

        Assertions.assertEquals(Status.Code.ALREADY_EXISTS, exception.getStatus().getCode());

        Long count = mentorTagRepository.count().block();
        Assertions.assertEquals(1L, count);
    }

    @Test
    void listMentorTags_success_returnsAllFromDatabase() {
        mentorTagRepository.saveAll(List.of(
                constructListMentorTagEntity("Java"),
                constructListMentorTagEntity("1C: Предприятие"),
                constructListMentorTagEntity("Kotlin")
        )).collectList().block();

        AllMentorTagsRequset request = constructAllMentorTagsRequest();

        AllMentorTagsResponse response = stub.listMentorTags(Mono.just(request))
                                             .block();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(3, response.getAllMentorsTagsList().size());
    }

    @Test
    void listMentorTags_whenDatabaseEmpty_returnsEmptyList() {
        AllMentorTagsRequset request = constructAllMentorTagsRequest();

        AllMentorTagsResponse response = stub.listMentorTags(Mono.just(request))
                                             .block();

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getAllMentorsTagsList().isEmpty());
    }

    @Test
    void attachMentorTags_success_savedToDatabase() {

        UserEntity mentor = constructMentorUserEntityWithoutId();
        mentor = userRepository.save(mentor).block();
        List<Long> tagIds = mentorTagRepository.saveAll(List.of(
                constructListMentorTagEntity("Java"),
                constructListMentorTagEntity("Kotlin")
        )).map(tag -> tag.getId()).collectList().block();

        AttachMentorTagsRequest request = constructAttachMentorTagsRequest(mentor.getId(), tagIds);

        AttachMentorTagsResponse response = stub.attachMentorTags(Mono.just(request))
                                                .block();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.getAttachedTagIdsList().size());
        Assertions.assertTrue(response.getNotAttachedTagIdsList().isEmpty());

        Long linksCount = mentorTagLinkRepository.count().block();
        Assertions.assertEquals(2L, linksCount);
    }

    @Test
    void attachMentorTags_whenTagsAlreadyAttached_theyAppearInNotAttached() {
        UserEntity mentor = constructMentorUserEntityWithoutId();
        userRepository.save(mentor).block();

        List<Long> tagIds = mentorTagRepository.saveAll(List.of(
                constructListMentorTagEntity("Java"),
                constructListMentorTagEntity("Kotlin")
        )).map(tag -> tag.getId()).collectList().block();

        AttachMentorTagsRequest request = constructAttachMentorTagsRequest(mentor.getId(), tagIds);

        stub.attachMentorTags(Mono.just(request)).block();

        AttachMentorTagsResponse response = stub.attachMentorTags(Mono.just(request))
                                                .block();

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getAttachedTagIdsList().isEmpty());
        response.getNotAttachedTagIdsList().forEach( id ->
                                                             System.out.println(id)
        );
        Assertions.assertEquals(2, response.getNotAttachedTagIdsList().size());

        Long linksCount = mentorTagLinkRepository.count().block();
        Assertions.assertEquals(2L, linksCount);
    }

    @Test
    void attachMentorTags_whenMentorNotFound_thenNotFoundStatus() {
        AttachMentorTagsRequest request = constructAttachMentorTagsRequest();

        StatusRuntimeException exception = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> stub.attachMentorTags(Mono.just(request)).block()
        );

        Assertions.assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode());
    }

    @Test
    void attachMentorTags_whenUserIsNotMentor_thenInvalidArgument() {
        UserEntity user = constructRegularUserEntityWithoutId();
        userRepository.save(user).block();

        AttachMentorTagsRequest request = constructAttachMentorTagsRequest(user.getId());

        StatusRuntimeException exception = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> stub.attachMentorTags(Mono.just(request)).block()
        );

        Assertions.assertEquals(Code.INVALID_ARGUMENT, exception.getStatus().getCode());
    }

    @Test
    void detachMentorTag_success_removedFromDatabase() {
        UserEntity mentor = constructMentorUserEntityWithoutId();
        userRepository.save(mentor).block();

        MentorTagEntity tag = mentorTagRepository.save(constructTestMentorTagEntityWithoutId()).block();

        mentorTagLinkRepository.save(
                MentorTagLinkEntity.builder()
                                   .mentorId(mentor.getId())
                                   .tagId(tag.getId())
                                   .build()
        ).block();

        Assertions.assertEquals(1L, mentorTagLinkRepository.count().block());

        DetachMentorTagRequest request = constructDetachMentorTagRequest(mentor.getId(), tag.getId());

        DetachMentorTagResponse response = stub.detachMentorTag(Mono.just(request))
                                               .block();

        Assertions.assertNotNull(response);

        Assertions.assertEquals(0L, mentorTagLinkRepository.count().block());
    }
}
