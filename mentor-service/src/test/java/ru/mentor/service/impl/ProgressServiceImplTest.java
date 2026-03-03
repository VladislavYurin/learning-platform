package ru.mentor.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.MenteeProgressDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserCourseAccessEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.CustomAccessDeniedException;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.repository.*;
import ru.mentor.testUtil.TestEntityStubGenerator;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProgressServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private UserCourseAccessRepository userCourseAccessRepository;

    @Mock
    private UserModuleAccessRepository userModuleAccessRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProgressServiceImpl progressService;

    private static final Long MENTOR_ID = 1L;
    private static final Long COURSE_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long ADMIN_ID = 99L;
    private static final Long OTHER_MENTOR_ID = 98L;

    private UserEntity mentor;
    private CourseEntity course;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        mentor = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);
        course = TestEntityStubGenerator.constructCourseEntity();
        course.setAuthor(mentor);
        user = UserEntity.builder()
                .id(USER_ID)
                .username("студент@test")
                .firstName("Брэд")
                .lastName("Пит")
                .role(Role.USER)
                .build();

    }

    @Test
    void getCourseProgressByMentor_mentorIsAuthor_returnsResponseWithMenteesAndStatistic() {
        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);

        UserCourseAccessEntity access = UserCourseAccessEntity.builder()
                .id(1L)
                .user(user)
                .course(course)
                .accessGrantedBy(mentor)
                .build();
        Mockito.when(userCourseAccessRepository.findAllByCourseId(COURSE_ID))
                .thenReturn(List.of(access));

        Mockito.when(moduleRepository.findAllByCourseIdOrderByModuleOrderNumberAsc(COURSE_ID))
                .thenReturn(Collections.emptyList());
        Mockito.when(userModuleAccessRepository.findAllByUserIdAndCourseId(USER_ID, COURSE_ID))
                .thenReturn(Collections.emptyList());

        CourseProgressResponse response = progressService.getCourseProgressByMentor(MENTOR_ID, COURSE_ID);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(COURSE_ID, response.getCourseId());
        Assertions.assertNotNull(response.getMentee());
        Assertions.assertEquals(1, response.getMentee().size());
        Assertions.assertNotNull(response.getStatistic());
        Assertions.assertEquals(1, response.getStatistic().getTotalMenteeCount());
    }

    @Test
    void getCourseProgressByMentor_mentorIsAdmin_returnsResponse() {
        UserEntity admin = UserEntity.builder()
                .id(ADMIN_ID)
                .username("admin@test")
                .firstName("Админ")
                .lastName("Административный")
                .role(Role.ADMIN)
                .build();
        Mockito.when(userRepository.findByIdOrThrow(ADMIN_ID)).thenReturn(admin);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);
        Mockito.when(userCourseAccessRepository.findAllByCourseId(COURSE_ID))
                .thenReturn(Collections.emptyList());
        Mockito.when(moduleRepository.findAllByCourseIdOrderByModuleOrderNumberAsc(COURSE_ID))
                .thenReturn(Collections.emptyList());

        CourseProgressResponse response = progressService.getCourseProgressByMentor(ADMIN_ID, COURSE_ID);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(COURSE_ID, response.getCourseId());
        Assertions.assertNotNull(response.getMentee());
        Assertions.assertTrue(response.getMentee().isEmpty());
        Assertions.assertNotNull(response.getStatistic());
        Assertions.assertEquals(0, response.getStatistic().getTotalMenteeCount());
    }

    @Test
    void getCourseProgressByMentor_mentorNotAuthor_throwsCustomAccessDeniedException() {
        UserEntity otherMentor = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);
        otherMentor.setId(OTHER_MENTOR_ID);
        CourseEntity courseOtherAuthor = TestEntityStubGenerator.constructCourseEntity();
        courseOtherAuthor.setId(COURSE_ID);
        courseOtherAuthor.setAuthor(otherMentor);

        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(courseOtherAuthor);

        Assertions.assertThrows(CustomAccessDeniedException.class,
                () -> progressService.getCourseProgressByMentor(MENTOR_ID, COURSE_ID));
    }

    @Test
    void getCourseProgressByMentor_courseNotFound_throwsEntityNotFoundException() {
        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID))
                .thenThrow(new EntityNotFoundException("Курс не найден"));

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> progressService.getCourseProgressByMentor(MENTOR_ID, COURSE_ID));
    }

    @Test
    void getCourseProgressByMentor_mentorNotFound_throwsEntityNotFoundException() {
        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID))
                .thenThrow(new EntityNotFoundException("Пользователь не найден"));

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> progressService.getCourseProgressByMentor(MENTOR_ID, COURSE_ID));
    }

    @Test
    void getCourseProgressByMentor_noMentees_returnsEmptyMenteeListAndZeroStatistic() {
        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);
        Mockito.when(userCourseAccessRepository.findAllByCourseId(COURSE_ID))
                .thenReturn(Collections.emptyList());
        Mockito.when(moduleRepository.findAllByCourseIdOrderByModuleOrderNumberAsc(COURSE_ID))
                .thenReturn(Collections.emptyList());

        CourseProgressResponse response = progressService.getCourseProgressByMentor(MENTOR_ID, COURSE_ID);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(COURSE_ID, response.getCourseId());
        Assertions.assertNotNull(response.getMentee());
        Assertions.assertTrue(response.getMentee().isEmpty());
        Assertions.assertNotNull(response.getStatistic());
        Assertions.assertEquals(0, response.getStatistic().getTotalMenteeCount());
    }

    @Test
    void getAllUsersAtCourse_mentorIsAuthor_returnsNonEmptyListOfMenteeProgressDto() {
        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);

        UserCourseAccessEntity access = UserCourseAccessEntity.builder()
                .id(1L)
                .user(user)
                .course(course)
                .accessGrantedBy(mentor)
                .build();
        Mockito.when(userCourseAccessRepository.findAllByCourseId(COURSE_ID))
                .thenReturn(List.of(access));
        Mockito.when(moduleRepository.findAllByCourseIdOrderByModuleOrderNumberAsc(COURSE_ID))
                .thenReturn(Collections.emptyList());
        Mockito.when(userModuleAccessRepository.findAllByUserIdAndCourseId(USER_ID, COURSE_ID))
                .thenReturn(Collections.emptyList());

        List<MenteeProgressDto> result = progressService.getAllUsersAtCourse(MENTOR_ID, COURSE_ID);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(USER_ID, result.getFirst().getUserId());
        Assertions.assertEquals("Брэд", result.getFirst().getFirstName());
        Assertions.assertEquals("Пит", result.getFirst().getLastName());
    }

    @Test
    void getAllUsersAtCourse_mentorIsAdmin_returnsList() {
        UserEntity admin = UserEntity.builder()
                .id(ADMIN_ID)
                .username("admin@test")
                .firstName("Админ")
                .lastName("Административный")
                .role(Role.ADMIN)
                .build();
        Mockito.when(userRepository.findByIdOrThrow(ADMIN_ID)).thenReturn(admin);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);
        Mockito.when(userCourseAccessRepository.findAllByCourseId(COURSE_ID))
                .thenReturn(Collections.emptyList());
        Mockito.when(moduleRepository.findAllByCourseIdOrderByModuleOrderNumberAsc(COURSE_ID))
                .thenReturn(Collections.emptyList());

        List<MenteeProgressDto> result = progressService.getAllUsersAtCourse(ADMIN_ID, COURSE_ID);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsersAtCourse_mentorNotAuthor_throwsCustomAccessDeniedException() {
        UserEntity otherMentor = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);
        otherMentor.setId(OTHER_MENTOR_ID);
        CourseEntity courseOtherAuthor = TestEntityStubGenerator.constructCourseEntity();
        courseOtherAuthor.setId(COURSE_ID);
        courseOtherAuthor.setAuthor(otherMentor);

        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(courseOtherAuthor);

        Assertions.assertThrows(CustomAccessDeniedException.class,
                () -> progressService.getAllUsersAtCourse(MENTOR_ID, COURSE_ID));
    }

    @Test
    void getAllUsersAtCourse_mentorNotFound_throwsEntityNotFoundException() {
        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID))
                .thenThrow(new EntityNotFoundException("Пользователь не найден"));

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> progressService.getAllUsersAtCourse(MENTOR_ID, COURSE_ID));
    }

    @Test
    void getAllUsersAtCourse_courseNotFound_throwsEntityNotFoundException() {
        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID))
                .thenThrow(new EntityNotFoundException("Курс не найден"));

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> progressService.getAllUsersAtCourse(MENTOR_ID, COURSE_ID));
    }

    @Test
    void getAllUsersAtCourse_noUsersAtCourse_returnsEmptyList() {
        Mockito.when(userRepository.findByIdOrThrow(MENTOR_ID)).thenReturn(mentor);
        Mockito.when(courseRepository.findByIdOrThrow(COURSE_ID)).thenReturn(course);
        Mockito.when(userCourseAccessRepository.findAllByCourseId(COURSE_ID))
                .thenReturn(Collections.emptyList());
        Mockito.when(moduleRepository.findAllByCourseIdOrderByModuleOrderNumberAsc(COURSE_ID))
                .thenReturn(Collections.emptyList());

        List<MenteeProgressDto> result = progressService.getAllUsersAtCourse(MENTOR_ID, COURSE_ID);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}


