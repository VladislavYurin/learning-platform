package ru.mentor.services.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import ru.mentor.constant.MdcKeys;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.MenteeProgressDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.MentorClient;
import ru.mentor.services.RedirectProgressService;
import ru.mentor.services.UserService;

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
     *
     * @param courseId
     *         идентификатор курса
     *
     * @return сводный отчет по прогрессу курса
     */
    @Override
    public CourseProgressResponse getCourseProgressByMentor(Long courseId) {
        UserEntity user = userService.getCurrentUser();
        Long userId = user.getId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");

        log.debug(
                "[userId={}] Получен запрос на получение прогресса учеников в курсе [courseId={}].",
                userId,
                courseId
        );

        try {
            CourseProgressResponse response =
                    mentorClient.getCourseProgressByMentor(requestId, userId, courseId).getBody();

            log.debug(
                    "[userId={}] Успешно получен ответ от mentor-service на получение прогресса учеников в курсе [courseId={}].",
                    userId,
                    courseId
            );

            return response;
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при вызове mentor-service во время получения прогресса учеников в курсе [courseId={}].",
                    userId,
                    courseId,
                    e
            );
            throw e;
        }
    }

    /**
     * Возвращает список прогресса всех учеников курса для текущего наставника.
     *
     * @param courseId
     *         идентификатор курса
     *
     * @return список прогресса по каждому ученику
     */
    @Override
    public List<MenteeProgressDto> getAllUsersAtCourse(Long courseId) {
        UserEntity user = userService.getCurrentUser();
        Long userId = user.getId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");

        log.debug(
                "[userId={}] Получен запрос на получение всех учеников в курсе [courseId={}].",
                userId,
                courseId
        );

        try {
            List<MenteeProgressDto> response =
                    mentorClient.getAllUsersAtCourse(requestId, userId, courseId).getBody();

            log.debug(
                    "[userId={}] Успешно получен ответ от mentor-service на получение всех учеников в курсе [courseId={}].",
                    userId,
                    courseId
            );

            return response;
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при вызове mentor-service во время получения всех учеников в курсе [courseId={}].",
                    userId,
                    courseId,
                    e
            );
            throw e;
        }
    }
}
