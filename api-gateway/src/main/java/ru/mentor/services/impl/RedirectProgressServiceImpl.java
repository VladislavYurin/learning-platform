package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.AccessClient;
import ru.mentor.services.RedirectProgressService;
import ru.mentor.services.UserService;

@Service
@RequiredArgsConstructor
public class RedirectProgressServiceImpl implements RedirectProgressService {

    private final AccessClient accessClient;

    private final UserService userService;

    @Override
    public CourseProgressResponse getCourseProgressByMentor(Long courseId) {
        UserEntity user = userService.getCurrentUser();
        Role.checkUserIsAdminOrMentor(user);
        return accessClient.getCourseProgressByMentor(user.getId(), courseId).getBody();
    }

}
