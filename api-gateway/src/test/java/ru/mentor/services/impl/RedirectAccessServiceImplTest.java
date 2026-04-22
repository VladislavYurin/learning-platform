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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.mentor.constant.Role;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.dto.front.CourseAccessRequest;
import ru.mentor.dto.front.ModuleAccessRequest;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.MentorClient;
import ru.mentor.mapper.AccessMapper;
import ru.mentor.services.UserService;

@ExtendWith(MockitoExtension.class)
class RedirectAccessServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private AccessMapper accessMapper;

    @Mock
    private MentorClient mentorClient;

    @InjectMocks
    private RedirectAccessServiceImpl redirectAccessService;

    private static final Long USER_ID = 1L;
    private static final Long COURSE_ID = 10L;
    private static final Long MODULE_ID = 20L;

    private UserEntity currentUser;

    @BeforeEach
    void setUp() {
        currentUser = UserEntity.builder()
                .id(USER_ID)
                .username("mentor@test")
                .role(Role.MENTOR)
                .build();
    }

    @Test
    void giveCourseAccess_delegatesToMentorClient_returnsResponse() {
        CourseAccessRequest request = new CourseAccessRequest();
        request.setUserId(USER_ID);
        request.setCourseId(COURSE_ID);

        GetAccessRequest innerRequest = GetAccessRequest.builder()
                .mentorId(currentUser.getId())
                .userId(USER_ID)
                .courseId(COURSE_ID)
                .moduleId(null)
                .build();

        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(accessMapper.mapToGetAccessRequest(currentUser, request)).thenReturn(innerRequest);
        Mockito.when(mentorClient.giveCourseAccess(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(GetAccessRequest.class)
                )).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> result = redirectAccessService.giveCourseAccess(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void revokeCourseAccess_delegatesToMentorClient_returnsResponse() {
        CourseAccessRequest request = new CourseAccessRequest();
        request.setUserId(USER_ID);
        request.setCourseId(COURSE_ID);

        GetAccessRequest innerRequest = GetAccessRequest.builder()
                .mentorId(currentUser.getId())
                .userId(USER_ID)
                .courseId(COURSE_ID)
                .moduleId(null)
                .build();

        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(accessMapper.mapToGetAccessRequest(currentUser, request)).thenReturn(innerRequest);
        Mockito.when(mentorClient.revokeCourseAccess(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(GetAccessRequest.class)
        )).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> result = redirectAccessService.revokeCourseAccess(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void giveModuleAccess_delegatesToMentorClient_returnsResponse() {
        ModuleAccessRequest request = new ModuleAccessRequest();
        request.setUserId(USER_ID);
        request.setCourseId(COURSE_ID);
        request.setModuleId(MODULE_ID);

        GetAccessRequest innerRequest = GetAccessRequest.builder()
                .mentorId(currentUser.getId())
                .userId(USER_ID)
                .courseId(COURSE_ID)
                .moduleId(MODULE_ID)
                .build();

        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(accessMapper.mapToGetAccessRequest(currentUser, request)).thenReturn(innerRequest);
        Mockito.when(mentorClient.giveModuleAccess(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(GetAccessRequest.class)
        )).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> result = redirectAccessService.giveModuleAccess(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void revokeModuleAccess_delegatesToMentorClient_returnsResponse() {
        ModuleAccessRequest request = new ModuleAccessRequest();
        request.setUserId(USER_ID);
        request.setCourseId(COURSE_ID);
        request.setModuleId(MODULE_ID);

        GetAccessRequest innerRequest = GetAccessRequest.builder()
                .mentorId(currentUser.getId())
                .userId(USER_ID)
                .courseId(COURSE_ID)
                .moduleId(MODULE_ID)
                .build();

        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(accessMapper.mapToGetAccessRequest(currentUser, request)).thenReturn(innerRequest);
        Mockito.when(mentorClient.revokeModuleAccess(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(GetAccessRequest.class)
        )).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> result = redirectAccessService.revokeModuleAccess(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
