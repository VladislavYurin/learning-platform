package ru.mentor;

import java.util.concurrent.CompletableFuture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.kafka.CourseAccessGrantedNotificationPayload;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.repository.UserCourseAccessRepository;
import ru.mentor.testUtil.CommonTestUtil;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestDataGenerator;

@Testcontainers
@SpringBootTest(classes = MentorApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AccessControllerTest {

    private static final String TOPIC = "notification-topic";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CommonTestUtil commonTestUtil;
    @Autowired
    private UserCourseAccessRepository userCourseAccessRepository;

    @Value("${microservice.auth-key}")
    private String API_KEY;

    // Поднимаем только Postgres
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    // Подменяем реальный KafkaTemplate мок-объектом
    @MockBean
    private KafkaTemplate<String, KafkaNotificationDto> kafkaTemplate;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        // DB
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        r.add("spring.liquibase.enabled", () -> "false");

        r.add("spring.kafka.bootstrap-servers", () -> "dummy:9092");
        r.add(
                "spring.kafka.producer.key-serializer",
                () -> "org.apache.kafka.common.serialization.StringSerializer"
        );
        r.add(
                "spring.kafka.producer.value-serializer",
                () -> "org.springframework.kafka.support.serializer.JsonSerializer"
        );
        r.add("spring.kafka.producer.properties.spring.json.add.type.headers", () -> "false");
    }

    @Test
    void getCourseAccessToUser_callsKafkaTemplateAndPersistsAccess() throws Exception {
        CompletableFuture<org.springframework.kafka.support.SendResult<String, KafkaNotificationDto>> done =
                new CompletableFuture<>();
        done.complete(null);
        Mockito.when(kafkaTemplate.send(
                       ArgumentMatchers.eq(TOPIC),
                       ArgumentMatchers.anyString(),
                       ArgumentMatchers.any(KafkaNotificationDto.class)
               ))
               .thenReturn(done);

        UserEntity mentor = commonTestUtil.createUser(TestDataGenerator.getMentorEntity());
        UserEntity mentee = commonTestUtil.createUser(TestDataGenerator.getUserEntity());
        CourseEntity course = commonTestUtil.createCourse(
                TestConstantHolder.courseTitle,
                TestConstantHolder.courseDescription,
                mentor);

        String requestId = TestConstantHolder.requestId;

        mockMvc.perform(MockMvcRequestBuilders.post("/access/course/get-access")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .header("requestId", requestId)
                                              .header("X-Service-Auth", API_KEY)
                                              .content(String.format(
                                                      """
                                                              {
                                                                "mentorId": %d,
                                                                "userId": %d,
                                                                "courseId": %d,
                                                                "moduleId": null
                                                              }
                                                              """,
                                                      mentor.getId(),
                                                      mentee.getId(),
                                                      course.getId()
                                              )))
               .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertThat(userCourseAccessRepository
                                      .existsByUserIdAndCourseId(mentee.getId(), course.getId()))
                                      .isTrue();

        ArgumentCaptor<KafkaNotificationDto> dtoCaptor = ArgumentCaptor.forClass(
                KafkaNotificationDto.class);

        Mockito.verify(kafkaTemplate, Mockito.times(1))
               .send(ArgumentMatchers.eq(TOPIC), ArgumentMatchers.anyString(), dtoCaptor.capture());

        KafkaNotificationDto sent = dtoCaptor.getValue();
        Assertions.assertThat(sent).isNotNull();
        Assertions.assertThat(sent.getNotificationType())
                  .isEqualTo(NotificationTypeEnum.COURSE_ACCESS_GRANTED);
        Assertions.assertThat(sent.getUserInfo()).isNotNull();
        Assertions.assertThat(sent.getUserInfo().getUsername()).isEqualTo(TestConstantHolder.username);
        Assertions.assertThat(sent.getPayload())
                  .isInstanceOf(CourseAccessGrantedNotificationPayload.class);
        CourseAccessGrantedNotificationPayload payload =
                (CourseAccessGrantedNotificationPayload) sent.getPayload();
        Assertions.assertThat(payload.getCourseTitle()).isEqualTo(TestConstantHolder.courseTitle);
        Assertions.assertThat(payload.getAccessGrantedBy()).isNotNull();
        Assertions.assertThat(payload.getAccessGrantedBy().getUsername()).isEqualTo(
                TestConstantHolder.mentorName);
        Assertions.assertThat(payload.getAccessGrantedAt()).isNotNull();
    }

}