package ru.mentor.services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.CourseProgressStatisticDto;
import ru.mentor.dto.MenteeProgressDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.MentorClient;
import ru.mentor.services.UserService;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class RedirectProgressServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private MentorClient mentorClient;

    @InjectMocks
    private RedirectProgressServiceImpl redirectProgressService;

    private static final Long COURSE_ID = 1L;

    private UserEntity currentUser;

    @BeforeEach
    void setUp() {
        currentUser = UserEntity.builder()
                .id(1L)
                .username("mentor@test")
                .role(Role.MENTOR)
                .build();

    }

    @Test
    void getCourseProgressByMentor_delegatesToMentorClient_returnBody(){
        CourseProgressResponse expected = CourseProgressResponse.builder()
                .courseId(COURSE_ID)
                .courseTitle("Тест курс")
                .mentee(List.of())
                .statistic(CourseProgressStatisticDto.builder().totalMenteeCount(0).
                        moduleDistribution(Map.of()).build())
                .build();

        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(mentorClient.getCourseProgressByMentor(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(currentUser.getId()),
                ArgumentMatchers.eq(COURSE_ID)
        )).thenReturn(ResponseEntity.ok(expected));

        CourseProgressResponse result = redirectProgressService.getCourseProgressByMentor(COURSE_ID);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(COURSE_ID, result.getCourseId());
        Assertions.assertEquals("Тест курс", result.getCourseTitle());

    }

    @Test
    void getAllUsersAtCourse_delegatesToMentorClient_returnBody(){
        List<MenteeProgressDto> expected = List.of(
                MenteeProgressDto.builder()
                        .userId(2L)
                        .firstName("Влад")
                        .lastName("Юрин")
                        .build()
        );

        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(mentorClient.getAllUsersAtCourse(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(currentUser.getId()),
                ArgumentMatchers.eq(COURSE_ID)
        )).thenReturn(ResponseEntity.ok(expected));

        List<MenteeProgressDto> result = redirectProgressService.getAllUsersAtCourse(COURSE_ID);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(2L, result.getFirst().getUserId());

    }
}
