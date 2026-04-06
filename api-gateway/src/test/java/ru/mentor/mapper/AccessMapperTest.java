package ru.mentor.mapper;

import org.junit.jupiter.api.Test;
import ru.mentor.constant.Role;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.dto.front.CourseAccessRequest;
import ru.mentor.dto.front.ModuleAccessRequest;
import ru.mentor.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AccessMapperTest {

    private final AccessMapper accessMapper = new AccessMapperImpl();

    @Test
    void mapToGetAccessRequest_courseRequest_success() {
        UserEntity mentor = UserEntity.builder()
                .id(1L)
                .username("mentor@test")
                .role(Role.MENTOR)
                .build();

        CourseAccessRequest request = new CourseAccessRequest();
        request.setUserId(2L);
        request.setCourseId(10L);

        GetAccessRequest result = accessMapper.mapToGetAccessRequest(mentor, request);

        assertNotNull(result);
        assertEquals(mentor.getId(), result.getMentorId());
        assertEquals(request.getUserId(), result.getUserId());
        assertEquals(request.getCourseId(), result.getCourseId());
        assertNull(result.getModuleId());
    }

    @Test
    void mapToGetAccessRequest_moduleRequest_success() {
        UserEntity mentor = UserEntity.builder()
                .id(1L)
                .username("mentor@test")
                .role(Role.MENTOR)
                .build();

        ModuleAccessRequest request = new ModuleAccessRequest();
        request.setUserId(2L);
        request.setCourseId(10L);
        request.setModuleId(20L);

        GetAccessRequest result = accessMapper.mapToGetAccessRequest(mentor, request);

        assertNotNull(result);
        assertEquals(mentor.getId(), result.getMentorId());
        assertEquals(request.getUserId(), result.getUserId());
        assertEquals(request.getCourseId(), result.getCourseId());
        assertEquals(request.getModuleId(), result.getModuleId());
    }
}
