package ru.mentor.grpc;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.mentor.constant.Role;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.CustomAccessDeniedException;

@Component
public class MentorAccessValidator {

    /**
     * Проверяет, что пользователь является автором курса или администратором.
     *
     * @param requestId
     *          Идентификатор запроса, ассоциированный с текущей сессией.
     *
     * @param mentor
     *          Наставник, запрашивающий доступ.
     *
     * @param course
     *          Курс, для которого требуется доступ.
     *
     * @throws CustomAccessDeniedException
     *          Если у наставника нет прав на выдачу доступа к курсу.
     */

    public Mono<Void> checkUserIsAuthorOrAdmin(
            String requestId,
            UserEntity mentor,
            CourseEntity course
    ) {
        // Проверяем, что юзер является автором курса
        if (Role.checkIsAdmin(mentor)) {
            return Mono.empty();
        }

        // Проверяем, что юзер является автором курса
        if (Role.checkIsMentor(mentor) && Role.checkMentorIsAuthorOfCourse(mentor, course)) {
            return Mono.empty();
        }

        // Если юзер не является автором курса, выбрасываем исключение
        return Mono.error(new CustomAccessDeniedException(
                String.format(
                        "Юзер с ID = %d не имеет доступа к выдаче доступа к курсу %d",
                        mentor.getId(),
                        course.getId()
                ), requestId
        ));
    }
}