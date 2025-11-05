package ru.mentor.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.gateway.model.CourseAccessRequest;
import ru.mentor.gateway.model.ModuleAccessRequest;
import ru.mentor.entity.UserEntity;

/**
 * Маппер для формирования внутреннего запроса на выдачу доступа.
 * <p>
 *     Преобразует данные наставника и внешнего запроса {@link CourseAccessRequest}
 *     в DTO {@link GetAccessRequest}, используемое во внешнем сервисе доступа.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class AccessMapper {

    /**
     * Формирует внутренний запрос на выдачу доступа из данных пользователя-наставника и входящего запроса.
     * @param user пользователь, прошедший аутентификацию (наставник), источник - {@code mentorId}
     * @param request входящий запрос с идентификаторами пользователя/модуля/курса
     * @return собранный {@link GetAccessRequest} для вызова внешнего сервиса
     */
    public GetAccessRequest mapToGetAccessRequest(UserEntity user, CourseAccessRequest request) {
        return GetAccessRequest.builder()
                               .mentorId(user.getId())
                               .userId(request.getUserId())
                               .moduleId(null)
                               .courseId(request.getCourseId())
                               .build();
    }

    public GetAccessRequest mapToGetAccessRequest(UserEntity user, ModuleAccessRequest request) {
        return GetAccessRequest.builder()
                .mentorId(user.getId())
                .userId(request.getUserId())
                .moduleId(request.getModuleId())
                .courseId(request.getCourseId())
                .build();
    }

}
