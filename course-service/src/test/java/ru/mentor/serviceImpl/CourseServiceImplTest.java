package ru.mentor.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.CourseDtoWithoutModules;
import ru.mentor.dto.InnerCreateCourseRequest;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.entity.CourseTagLinkEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.CustomAccessDeniedException;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.CourseTagRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.impl.CourseServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;

@ExtendWith(MockitoExtension.class)
public class CourseServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    CourseRepository courseRepository;

    @Mock
    CourseTagRepository tagRepository;

    @Mock
    ru.mentor.kafka.KafkaFacade kafkaFacade;

    @Mock
    ru.mentor.util.AccessChecker accessChecker;

    @Mock
    ru.mentor.repository.UserCourseAccessRepository userCourseAccessRepository;

    @Mock
    ru.mentor.repository.UserModuleAccessRepository userModuleAccessRepository;

    @Mock
    BaseMapper baseMapper;

    @InjectMocks
    CourseServiceImpl service;

    @Test
    @DisplayName("OK: ментор, теги добавляются уникально, маппинг результата корректный")
    void createCourse_ok_distinctTags() {

        InnerCreateCourseRequest request = Mockito.mock(InnerCreateCourseRequest.class);
        Mockito.when(request.getAuthorId()).thenReturn(TestConstantHolder.authorId);
        Mockito.when(request.getCourseName()).thenReturn(TestConstantHolder.courseTitle);
        Mockito.when(request.getCourseDescription())
               .thenReturn(TestConstantHolder.courseDescription);

        Mockito.when(request.getTagIds()).thenReturn(List.of(1L, 2L, 2L));

        UserEntity user = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);
        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.authorId)).thenReturn(user);

        try (MockedStatic<Role> mockedRole = Mockito.mockStatic(Role.class)) {
            mockedRole.when(() -> Role.checkIsMentor(user)).thenReturn(true);
            mockedRole.when(() -> Role.checkIsAdmin(user)).thenReturn(false);

            CourseTagEntity tagOne = TestEntityStubGenerator.constructCourseTagEntity();
            tagOne.setId(1L);
            CourseTagEntity tagTwo = TestEntityStubGenerator.constructCourseTagEntity();
            tagTwo.setId(2L);
            Mockito.when(tagRepository.getReferenceById(1L)).thenReturn(tagOne);
            Mockito.when(tagRepository.getReferenceById(2L)).thenReturn(tagTwo);

            ArgumentCaptor<CourseEntity> captor = ArgumentCaptor.forClass(CourseEntity.class);
            Mockito.when(courseRepository.save(Mockito.any(CourseEntity.class)))
                   .thenAnswer(inv -> inv.getArgument(0));

            CourseDto dtoExpected = CourseDto.builder()
                                             .id(1L)
                                             .createdAt(LocalDateTime.now())
                                             .tags(List.of())
                                             .build();
            Mockito.when(baseMapper.mapCourse(Mockito.any(CourseEntity.class), Mockito.eq(user),
                                              Mockito.eq(false), Mockito.eq(false), Mockito.eq(true)
                   ))
                   .thenReturn(dtoExpected);

            CourseDto dto = service.createCourse(request);

            Assertions.assertThat(dto).isSameAs(dtoExpected);

            Mockito.verify(courseRepository).save(captor.capture());
            CourseEntity saved = captor.getValue();
            Assertions.assertThat(saved.getCourseTags())
                      .hasSize(2)
                      .extracting(ct -> ct.getTag().getId())
                      .containsExactlyInAnyOrder(1L, 2L);

            for (CourseTagLinkEntity ct : saved.getCourseTags()) {
                Assertions.assertThat(ct.getCourse()).isSameAs(saved);
                Assertions.assertThat(ct.getTag().getId()).isIn(1L, 2L);
            }

            Mockito.verify(tagRepository, Mockito.times(1)).getReferenceById(1L);
            Mockito.verify(tagRepository, Mockito.times(1)).getReferenceById(2L);
            Mockito.verify(baseMapper).mapCourse(saved, user, false, false, true);
            Mockito.verifyNoMoreInteractions(tagRepository, baseMapper);
        }
    }

    @Test
    @DisplayName("OK: tagIds == null — курс создаётся без связей, getReferenceById не вызывается")
    void createCourse_nullTags_becomesEmpty() {
        InnerCreateCourseRequest request = Mockito.mock(InnerCreateCourseRequest.class);
        Mockito.when(request.getAuthorId()).thenReturn(TestConstantHolder.authorId);
        Mockito.when(request.getCourseName()).thenReturn(TestConstantHolder.courseTitle);
        Mockito.when(request.getCourseDescription())
               .thenReturn(TestConstantHolder.courseDescription);
        Mockito.when(request.getTagIds()).thenReturn(null);

        UserEntity user = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);
        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.authorId)).thenReturn(user);

        try (MockedStatic<Role> mockedRole = Mockito.mockStatic(Role.class)) {
            mockedRole.when(() -> Role.checkIsMentor(user)).thenReturn(true);
            mockedRole.when(() -> Role.checkIsAdmin(user)).thenReturn(false);

            ArgumentCaptor<CourseEntity> captor = ArgumentCaptor.forClass(CourseEntity.class);
            Mockito.when(courseRepository.save(Mockito.any(CourseEntity.class)))
                   .thenAnswer(inv -> inv.getArgument(0));

            CourseDto dtoExpected = CourseDto.builder()
                                             .id(1L)
                                             .courseTitle("Spring")
                                             .courseDescription("desc")
                                             .isActive(true)
                                             .createdAt(LocalDateTime.now())
                                             .tags(List.of())
                                             .build();
            Mockito.when(baseMapper.mapCourse(Mockito.any(CourseEntity.class), Mockito.eq(user),
                                              Mockito.eq(false), Mockito.eq(false), Mockito.eq(true)
                   ))
                   .thenReturn(dtoExpected);

            CourseDto dto = service.createCourse(request);
            Assertions.assertThat(dto).isNotNull();

            Mockito.verify(courseRepository).save(captor.capture());
            CourseEntity saved = captor.getValue();
            Assertions.assertThat(saved.getCourseTags()).isEmpty();

            Mockito.verify(tagRepository, Mockito.never()).getReferenceById(Mockito.anyLong());
        }
    }

    @Test
    @DisplayName("OK: Админ может удалить любой курс")
    void deleteCourse_whenAdmin_thenSuccess() {

        UserEntity admin = TestEntityStubGenerator.constructUserEntityWithRole(Role.ADMIN);
        CourseEntity course = TestEntityStubGenerator.constructCourseEntity();

        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.userId)).thenReturn(admin);
        Mockito.when(courseRepository.findByIdOrThrow(TestConstantHolder.courseId)).thenReturn(
                course);

        service.deleteCourse(TestConstantHolder.userId, TestConstantHolder.courseId);

        Mockito.verify(courseRepository).deleteById(TestConstantHolder.courseId);
    }

    @Test
    @DisplayName("OK: Ментор может удалять только свой курс")
    void deleteCourse_whenMentorDeletesOwnCourse_thenSuccess() {

        UserEntity mentor = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);
        CourseEntity course = TestEntityStubGenerator.constructCourseEntity();
        course.setAuthor(mentor);

        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.userId)).thenReturn(mentor);
        Mockito.when(courseRepository.findByIdOrThrow(TestConstantHolder.courseId)).thenReturn(
                course);

        service.deleteCourse(TestConstantHolder.userId, TestConstantHolder.courseId);

        Mockito.verify(courseRepository).delete(course);
    }

    @Test
    @DisplayName("OK: Ментор не может удалять курс другого ментора")
    void deleteCourse_whenMentorDeletesOtherCourse_thenThrowsException() {

        UserEntity mentor = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);
        UserEntity otherMentor = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);
        otherMentor.setId(2L);
        CourseEntity course = TestEntityStubGenerator.constructCourseEntity();
        course.setAuthor(otherMentor);

        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.userId)).thenReturn(mentor);
        Mockito.when(courseRepository.findByIdOrThrow(TestConstantHolder.courseId)).thenReturn(
                course);

        Assertions.assertThatThrownBy(() -> service.deleteCourse(
                          TestConstantHolder.userId,
                          TestConstantHolder.courseId
                  ))
                  .isInstanceOf(CustomAccessDeniedException.class)
                  .hasMessageContaining("не имеет доступа к удалению курса");

        Mockito.verify(courseRepository, Mockito.never()).delete(Mockito.any());
        Mockito.verify(courseRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    @DisplayName("OK: Обычный пользователь не может удалить курс")
    void deleteCourse_whenRegularUser_thenThrowsException() {

        UserEntity user = TestEntityStubGenerator.constructUserEntityWithRole(Role.USER);
        CourseEntity course = TestEntityStubGenerator.constructCourseEntity();

        Mockito.when(userRepository.findByIdOrThrow(TestConstantHolder.userId)).thenReturn(user);
        Mockito.when(courseRepository.findByIdOrThrow(TestConstantHolder.courseId)).thenReturn(
                course);

        Assertions.assertThatThrownBy(() -> service.deleteCourse(
                          TestConstantHolder.userId,
                          TestConstantHolder.courseId
                  ))
                  .isInstanceOf(CustomAccessDeniedException.class)
                  .hasMessageContaining("не имеет доступа к удалению курса");

        Mockito.verify(courseRepository, Mockito.never()).delete(Mockito.any());
        Mockito.verify(courseRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    void getAllActiveCoursesPreview_success_returnsResponse(){
        UserEntity mentorEntity = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);
        CourseDtoWithoutModules courseDto = TestEntityStubGenerator.constructCourseDtoWithoutModules();
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        courseEntity.setAuthor(mentorEntity);
        courseEntity.setIsActive(true);

        Mockito.when(courseRepository.findAllByIsActiveTrue())
                .thenReturn(List.of(courseEntity));

        Mockito.when(baseMapper.mapCoursesWithoutModules(
                            Mockito.anyList(),
                            Mockito.eq(false)))
                        .thenReturn(List.of(courseDto));

        List<CourseDtoWithoutModules> expectedResult = service.getAllActiveCoursesPreview();

        Assertions.assertThat(expectedResult).hasSize(1).containsExactly(courseDto);

        Mockito.verify(courseRepository).findAllByIsActiveTrue();
        Mockito.verify(baseMapper).mapCoursesWithoutModules(Mockito.anyList(), Mockito.eq(false));
        Mockito.verifyNoMoreInteractions(courseRepository, baseMapper);
    }

    @Test
    void getAllActiveCoursesPreview_noActiveCourses_returnsEmptyList() {
        Mockito.when(courseRepository.findAllByIsActiveTrue())
                .thenReturn(List.of());
        Mockito.when(baseMapper.mapCoursesWithoutModules(Mockito.anyList(), Mockito.eq(false)))
                .thenReturn(List.of());

        List<CourseDtoWithoutModules> expectedResult = service.getAllActiveCoursesPreview();

        Assertions.assertThat(expectedResult).isEmpty();

        Mockito.verify(courseRepository).findAllByIsActiveTrue();
        Mockito.verify(baseMapper).mapCoursesWithoutModules(Mockito.anyList(), Mockito.eq(false));
        Mockito.verifyNoMoreInteractions(courseRepository, baseMapper);
    }

    @Test
    void getAllActiveCoursesPreview_courseRepositoryReturnsMull_throwsException() {
        Mockito.when(courseRepository.findAllByIsActiveTrue()).thenReturn(null);
        Assertions.assertThatThrownBy(() ->
                service.getAllActiveCoursesPreview()).isInstanceOf(NullPointerException.class);
        Mockito.verify(courseRepository).findAllByIsActiveTrue();
        Mockito.verifyNoMoreInteractions(baseMapper);
    }
}