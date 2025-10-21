package ru.mentor.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.CourseClient;
import ru.mentor.services.impl.RedirectCourseServiceImpl;
import ru.mentor.testUtil.TestEntityStubGenerator;

import java.util.List;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class RedirectCourseServiceImplTest {

    @InjectMocks
    RedirectCourseServiceImpl redirectCourseService;

    @Mock
    UserService userService;

    @Mock
    CourseClient courseClient;

    @Test
    void getAllActiveCoursesPreview_success() {
        UserEntity userEntity = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);
        List<CourseDto> listCoursesDto =
                List.of(TestEntityStubGenerator.constructCourseDto());

        Mockito.when(userService.getCurrentUser()).thenReturn(userEntity);
        Mockito.when(courseClient.getAllActiveCoursesPreview(Mockito.anyString()))
                .thenReturn(listCoursesDto);

        List<CourseDto> actual = redirectCourseService.getAllActiveCoursesPreview();

        Assertions.assertEquals(listCoursesDto, actual);

        ArgumentCaptor<String> rqCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(courseClient).getAllActiveCoursesPreview(rqCaptor.capture());

        String rq = rqCaptor.getValue();

        Assertions.assertNotNull(rq);
        Assertions.assertFalse(rq.isBlank());
        Assertions.assertDoesNotThrow(() -> UUID.fromString(rq), "rqUId невалиден UUID");

        Mockito.verify(userService).getCurrentUser();
        Mockito.verifyNoMoreInteractions(userService, courseClient);
    }
}





