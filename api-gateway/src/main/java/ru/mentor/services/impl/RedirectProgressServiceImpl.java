package ru.mentor.services.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.MenteeProgressDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.MentorClient;
import ru.mentor.services.RedirectProgressService;
import ru.mentor.services.UserService;
import ru.mentor.util.RqGenerator;

/**
 * Реализация сервиса редиректов/агрегации прогресса по курсам.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectProgressServiceImpl implements RedirectProgressService {

    private final MentorClient mentorClient;

    private final UserService userService;

    /**
     * Возвращает агрегированную статистику прогресса по курсу для наставника.
     * @param courseId идентификатор курса
     * @return сводный отчет по прогрессу курса
     */
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

    /**
     * Возвращает список прогресса всех учеников курса для текущего наставника.
     * @param courseId идентификатор курса
     * @return список прогресса по каждому ученику
     */
    @Override
    public List<MenteeProgressDto> getAllUsersAtCourse(Long courseId) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на получение всех учеников в курсе [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                courseId,
                user.getId()
        ));
        Role.checkUserIsAdminOrMentor(user);
        return mentorClient.getAllUsersAtCourse(rqUId, user.getId(), courseId).getBody();
    }

}
