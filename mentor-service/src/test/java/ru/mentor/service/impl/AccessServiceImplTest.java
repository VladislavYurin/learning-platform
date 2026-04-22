package ru.mentor.service.impl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.constant.Role;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.entity.*;
import ru.mentor.exception.CustomAccessDeniedException;
import ru.mentor.exception.EntityAlreadyExistsException;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.kafka.KafkaFacade;
import ru.mentor.repository.*;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.util.AccessChecker;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccessServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccessChecker accessChecker;

    @Mock
    private UserCourseAccessRepository userCourseAccessRepository;

    @Mock
    private UserModuleAccessRepository userModuleAccessRepository;

    @Mock
    private KafkaFacade kafkaFacade;

    @InjectMocks
    private AccessServiceImpl accessService;

    private static final String REQUEST_ID = "req-1";
    private static final Long MENTOR_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long COURSE_ID = 1L;

    private UserEntity mentor;
    private UserEntity user;
    private CourseEntity course;
    private GetAccessRequest requestCourseOnly;

    @BeforeEach
    void setUp() {
        mentor = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);
        user = UserEntity.builder()
                .id(USER_ID)
                .username("студент@test")
                .firstName("Брэд")
                .lastName("Пит")
                .role(Role.USER)
                .build();
        course = TestEntityStubGenerator.constructCourseEntity();
        course.setAuthor(mentor);
        requestCourseOnly = GetAccessRequest.builder()
                .mentorId(MENTOR_ID)
                .userId(USER_ID)
                .courseId(COURSE_ID)
                .build();
    }

    @Test
    void getCourseAccessToUser_userHasNoAccess_savesAccessAndSendsKafkaMessage() {
        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userRepository.findByIdOrThrow(USER_ID)).thenReturn(user);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);
        Mockito.when(accessChecker.hasAccessToCourse(USER_ID, COURSE_ID)).thenReturn(false);

        UserCourseAccessEntity savedAccess = UserCourseAccessEntity.builder()
                .id(1L)
                .user(user)
                .course(course)
                .accessGrantedBy(mentor)
                .build();

        Mockito.when(userCourseAccessRepository.save(ArgumentMatchers.any(UserCourseAccessEntity.class)))
                .thenReturn(savedAccess);

        accessService.getCourseAccessToUser(REQUEST_ID, requestCourseOnly);
        Mockito.verify(userCourseAccessRepository, Mockito.times(1))
                .save(ArgumentMatchers.any(UserCourseAccessEntity.class));
        Mockito.verify(kafkaFacade, Mockito.times(1))
                .sendCourseAccessGrantedMessage(
                        ArgumentMatchers.any(UserEntity.class),
                        ArgumentMatchers.any(UserEntity.class),
                        ArgumentMatchers.any(CourseEntity.class),
                        ArgumentMatchers.any(UserCourseAccessEntity.class)
                );
    }

    @Test
    void getCourseAccessToUser_userAlreadyHasAccess_throwsEntityAlreadyExistsException() {
        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userRepository.findByIdOrThrow(USER_ID)).thenReturn(user);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);
        Mockito.when(accessChecker.hasAccessToCourse(USER_ID, COURSE_ID)).thenReturn(true);

        Assertions.assertThrows(EntityAlreadyExistsException.class,
                () -> accessService.getCourseAccessToUser(REQUEST_ID, requestCourseOnly));

        Mockito.verify(userCourseAccessRepository, Mockito.never()).save(ArgumentMatchers.any(UserCourseAccessEntity.class));
        Mockito.verifyNoInteractions(kafkaFacade);
    }

    @Test
    void getModuleAccessToUser_userHasCourseAccessAndNoModuleAccess_savesAccessAndSendsKafkaMessage() {
        ModuleEntity module = TestEntityStubGenerator.constructModuleEntity();
        module.setCourse(course);
        Long moduleId = module.getId();
        GetAccessRequest request = GetAccessRequest.builder()
                .mentorId(MENTOR_ID)
                .userId(USER_ID)
                .courseId(COURSE_ID)
                .moduleId(moduleId)
                .build();

        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userRepository.findByIdOrThrow(USER_ID)).thenReturn(user);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);
        Mockito.when(moduleRepository.findByIdOrThrow(moduleId)).thenReturn(module);

        Mockito.when(accessChecker.hasAccessToModule(USER_ID, moduleId)).thenReturn(false);
        Mockito.when(accessChecker.hasAccessToCourse(USER_ID, COURSE_ID)).thenReturn(true);

        UserModuleAccessEntity savedModuleAccess = UserModuleAccessEntity.builder()
                .id(1L)
                .user(user)
                .course(course)
                .module(module)
                .accessGrantedBy(mentor)
                .build();
        Mockito.when(userModuleAccessRepository.save(ArgumentMatchers.any(UserModuleAccessEntity.class)))
                .thenReturn(savedModuleAccess);

        accessService.getModuleAccessToUser(REQUEST_ID, request);

        Mockito.verify(userModuleAccessRepository, Mockito.times(1))
                .save(ArgumentMatchers.any(UserModuleAccessEntity.class));
        Mockito.verify(kafkaFacade, Mockito.times(1))
                .sendModuleAccessGrantedMessage(
                        ArgumentMatchers.any(UserEntity.class),
                        ArgumentMatchers.any(UserEntity.class),
                        ArgumentMatchers.any(CourseEntity.class),
                        ArgumentMatchers.any(ModuleEntity.class),
                        ArgumentMatchers.any(UserModuleAccessEntity.class)
                );
    }

    @Test
    void getModuleAccessToUser_userAlreadyHasModuleAccess_throwsEntityAlreadyExistsException() {
        ModuleEntity module = TestEntityStubGenerator.constructModuleEntity();
        module.setCourse(course);
        Long moduleId = module.getId();
        GetAccessRequest request = GetAccessRequest.builder()
                .mentorId(MENTOR_ID)
                .userId(USER_ID)
                .courseId(COURSE_ID)
                .moduleId(moduleId)
                .build();

        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userRepository.findByIdOrThrow(USER_ID)).thenReturn(user);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);
        Mockito.when(moduleRepository.findByIdOrThrow(moduleId)).thenReturn(module);

        Mockito.when(accessChecker.hasAccessToModule(USER_ID, moduleId)).thenReturn(true);

        Assertions.assertThrows(EntityAlreadyExistsException.class,
                () -> accessService.getModuleAccessToUser(REQUEST_ID, request));

        Mockito.verify(userModuleAccessRepository, Mockito.never())
                .save(ArgumentMatchers.any(UserModuleAccessEntity.class));
        Mockito.verifyNoInteractions(kafkaFacade);
    }

    @Test
    void getModuleAccessToUser_userHasNoCourseAccess_throwsEntityNotFoundException() {
        ModuleEntity module = TestEntityStubGenerator.constructModuleEntity();
        module.setCourse(course);
        Long moduleId = module.getId();
        GetAccessRequest request = GetAccessRequest.builder()
                .mentorId(MENTOR_ID)
                .userId(USER_ID)
                .courseId(COURSE_ID)
                .moduleId(moduleId)
                .build();

        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userRepository.findByIdOrThrow(USER_ID)).thenReturn(user);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);
        Mockito.when(moduleRepository.findByIdOrThrow(moduleId)).thenReturn(module);

        Mockito.when(accessChecker.hasAccessToModule(USER_ID, moduleId)).thenReturn(false);
        Mockito.when(accessChecker.hasAccessToCourse(USER_ID, COURSE_ID)).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> accessService.getModuleAccessToUser(REQUEST_ID, request));

        Mockito.verify(userModuleAccessRepository, Mockito.never())
                .save(ArgumentMatchers.any(UserModuleAccessEntity.class));
        Mockito.verifyNoInteractions(kafkaFacade);
    }

    @Test
    void deleteCourseAccessToUser_accessExists_deletesAndSendsKafkaMessage() {
        UserCourseAccessEntity existingAccess = UserCourseAccessEntity.builder()
                .id(1L)
                .user(user)
                .course(course)
                .accessGrantedBy(mentor)
                .build();

        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userRepository.findByIdOrThrow(USER_ID)).thenReturn(user);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);

        Mockito.when(accessChecker.hasAccessToCourse(USER_ID, COURSE_ID)).thenReturn(true);

        Mockito.when(userCourseAccessRepository.findByUserIdAndCourseId(USER_ID, COURSE_ID))
                .thenReturn(Optional.of(existingAccess));

        accessService.deleteCourseAccessToUser(REQUEST_ID, requestCourseOnly);

        Mockito.verify(userCourseAccessRepository, Mockito.times(1))
                .deleteByUserIdAndCourseId(USER_ID, COURSE_ID);
        Mockito.verify(userModuleAccessRepository, Mockito.times(1))
                .deleteAllByUserIdAndCourseId(USER_ID, COURSE_ID);
        Mockito.verify(kafkaFacade, Mockito.times(1))
                .sendCourseAccessRevokedMessage(
                        ArgumentMatchers.any(UserEntity.class),
                        ArgumentMatchers.any(UserEntity.class),
                        ArgumentMatchers.any(CourseEntity.class),
                        ArgumentMatchers.any(LocalDateTime.class)
                );
    }

    @Test
    void deleteCourseAccessToUser_accessNotFound_throwsEntityNotFoundException() {
        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userRepository.findByIdOrThrow(USER_ID)).thenReturn(user);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);

        Mockito.when(accessChecker.hasAccessToCourse(USER_ID, COURSE_ID)).thenReturn(true);

        Mockito.when(userCourseAccessRepository.findByUserIdAndCourseId(USER_ID, COURSE_ID))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> accessService.deleteCourseAccessToUser(REQUEST_ID, requestCourseOnly));

        Mockito.verify(userCourseAccessRepository, Mockito.never())
                .deleteByUserIdAndCourseId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        Mockito.verify(userModuleAccessRepository, Mockito.never())
                .deleteAllByUserIdAndCourseId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        Mockito.verifyNoInteractions(kafkaFacade);
    }

    @Test
    void deleteModuleAccessToUser_accessExists_deletesAndSendsKafkaMessage() {
        ModuleEntity module = TestEntityStubGenerator.constructModuleEntity();
        module.setCourse(course);
        Long moduleId = module.getId();
        GetAccessRequest request = GetAccessRequest.builder()
                .mentorId(MENTOR_ID)
                .userId(USER_ID)
                .courseId(COURSE_ID)
                .moduleId(moduleId)
                .build();

        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userRepository.findByIdOrThrow(USER_ID)).thenReturn(user);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);
        Mockito.when(moduleRepository.findByIdOrThrow(moduleId)).thenReturn(module);

        Mockito.when(accessChecker.hasAccessToCourse(USER_ID, COURSE_ID)).thenReturn(true);
        Mockito.when(accessChecker.hasAccessToModule(USER_ID, moduleId)).thenReturn(true);

        Mockito.when(userModuleAccessRepository.existsByUserIdAndModuleId(USER_ID, moduleId)).thenReturn(true);

        accessService.deleteModuleAccessToUser(REQUEST_ID, request);

        Mockito.verify(userModuleAccessRepository, Mockito.times(1))
                .deleteByUserIdAndModuleId(USER_ID, moduleId);
        Mockito.verify(kafkaFacade, Mockito.times(1))
                .sendModuleAccessRevokedMessage(
                        ArgumentMatchers.any(UserEntity.class),
                        ArgumentMatchers.any(UserEntity.class),
                        ArgumentMatchers.any(CourseEntity.class),
                        ArgumentMatchers.any(ModuleEntity.class),
                        ArgumentMatchers.any(LocalDateTime.class)
                );
    }

    @Test
    void deleteModuleAccessToUser_accessNotFound_throwsEntityNotFoundException() {
        ModuleEntity module = TestEntityStubGenerator.constructModuleEntity();
        module.setCourse(course);
        Long moduleId = module.getId();
        GetAccessRequest request = GetAccessRequest.builder()
                .mentorId(MENTOR_ID)
                .userId(USER_ID)
                .courseId(COURSE_ID)
                .moduleId(moduleId)
                .build();

        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userRepository.findByIdOrThrow(USER_ID)).thenReturn(user);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);
        Mockito.when(moduleRepository.findByIdOrThrow(moduleId)).thenReturn(module);

        Mockito.when(accessChecker.hasAccessToCourse(USER_ID, COURSE_ID)).thenReturn(true);
        Mockito.when(accessChecker.hasAccessToModule(USER_ID, moduleId)).thenReturn(true);

        Mockito.when(userModuleAccessRepository.existsByUserIdAndModuleId(USER_ID, moduleId)).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> accessService.deleteModuleAccessToUser(REQUEST_ID, request));

        Mockito.verify(userModuleAccessRepository, Mockito.never())
                .deleteByUserIdAndModuleId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        Mockito.verifyNoInteractions(kafkaFacade);
    }

    @Test
    void getCourseAccessToUser_mentorNotAuthor_throwsCustomAccessDeniedException() {
        UserEntity otherMentor = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);
        otherMentor.setId(99L);
        CourseEntity courseWithOtherAuthor = TestEntityStubGenerator.constructCourseEntity();
        courseWithOtherAuthor.setAuthor(otherMentor);
        Long courseIdOther = courseWithOtherAuthor.getId();
        GetAccessRequest request = GetAccessRequest.builder()
                .mentorId(MENTOR_ID)
                .userId(USER_ID)
                .courseId(courseIdOther)
                .build();

        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userRepository.findByIdOrThrow(USER_ID)).thenReturn(user);
        Mockito.when(courseRepository.findByIdOrThrow(courseIdOther)).thenReturn(courseWithOtherAuthor);

        Assertions.assertThrows(CustomAccessDeniedException.class,
                () -> accessService.getCourseAccessToUser(REQUEST_ID, request));

        Mockito.verify(userCourseAccessRepository, Mockito.never())
                .save(ArgumentMatchers.any(UserCourseAccessEntity.class));
        Mockito.verifyNoInteractions(kafkaFacade);
    }

    @Test
    void getModuleAccessToUser_moduleNotInCourse_throwsEntityNotFoundException() {
        CourseEntity otherCourse = TestEntityStubGenerator.constructCourseEntity();
        otherCourse.setId(999L);
        otherCourse.setAuthor(mentor);
        ModuleEntity module = TestEntityStubGenerator.constructModuleEntity();
        module.setCourse(otherCourse);
        Long moduleId = module.getId();
        GetAccessRequest request = GetAccessRequest.builder()
                .mentorId(MENTOR_ID)
                .userId(USER_ID)
                .courseId(COURSE_ID)
                .moduleId(moduleId)
                .build();

        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(userRepository.findByIdOrThrow(USER_ID)).thenReturn(user);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);
        Mockito.when(moduleRepository.findByIdOrThrow(moduleId)).thenReturn(module);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> accessService.getModuleAccessToUser(REQUEST_ID, request));

        Mockito.verify(userModuleAccessRepository, Mockito.never())
                .save(ArgumentMatchers.any(UserModuleAccessEntity.class));
        Mockito.verifyNoInteractions(kafkaFacade);
    }
}
