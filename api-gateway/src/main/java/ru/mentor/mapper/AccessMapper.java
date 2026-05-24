package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.dto.front.CourseAccessRequest;
import ru.mentor.dto.front.ModuleAccessRequest;
import ru.mentor.entity.UserEntity;

/**
 * Маппер для формирования внутреннего запроса на выдачу доступа.
 * <p>
 *     Преобразует данные наставника и внешнего запроса {@link CourseAccessRequest}
 *     в DTO {@link GetAccessRequest}, используемое во внешнем сервисе доступа.
 * </p>
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccessMapper {

    /**
     * Формирует внутренний запрос на выдачу доступа из данных пользователя-наставника и входящего запроса.
     * @param user пользователь, прошедший аутентификацию (наставник), источник - {@code mentorId}
     * @param request входящий запрос с идентификаторами пользователя/модуля/курса
     * @return собранный {@link GetAccessRequest} для вызова внешнего сервиса
     */
    @Mapping(target = "mentorId", source = "user.id")
    @Mapping(target = "userId", source = "request.userId")
    @Mapping(target = "courseId", source = "request.courseId")
    @Mapping(target = "moduleId", ignore = true)
    GetAccessRequest mapToGetAccessRequest(UserEntity user, CourseAccessRequest request);

    @Mapping(target = "mentorId", source = "user.id")
    @Mapping(target = "userId", source = "request.userId")
    @Mapping(target = "courseId", source = "request.courseId")
    @Mapping(target = "moduleId", source = "request.moduleId")
    GetAccessRequest mapToGetAccessRequest(UserEntity user, ModuleAccessRequest request);

}
