package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.MentorClient;
import ru.mentor.services.RedirectProgressService;
import ru.mentor.services.UserService;
import ru.mentor.util.RqGenerator;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectProgressServiceImpl implements RedirectProgressService {

    private final MentorClient mentorClient;

    private final UserService userService;

    @Override
    public CourseProgressResponse getCourseProgressByMentor(Long courseId) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на получение прогресса учеников в курсе [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                courseId,
                user.getId()
        ));
        Role.checkUserIsAdminOrMentor(user);
        return mentorClient.getCourseProgressByMentor(rqUId, user.getId(), courseId).getBody();
    }

}
