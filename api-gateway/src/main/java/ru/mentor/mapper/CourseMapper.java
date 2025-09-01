package ru.mentor.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentor.dto.InnerCreateCourseRequest;
import ru.mentor.dto.InnerCreateModuleRequest;
import ru.mentor.dto.front.CreateCourseRequest;
import ru.mentor.dto.front.CreateModuleRequest;

/**
 * Маппер для формирования внутреннего запроса на создание курса и модуля.
 */
@Component
@RequiredArgsConstructor
public class CourseMapper {

    /**
     * Формирует внутренний запрос на создание курса.
     * @param userId идентификатор автора курса
     * @param createCourseRequest входной запрос с названием и описанием курса
     * @return собранный запрос для вызова внешнего сервиса
     * @throws NullPointerException в случае, если запрос равен {@code null}
     */
    public InnerCreateCourseRequest mapToInnerCreateCourseRequest(
            Long userId,
            CreateCourseRequest createCourseRequest) {
        return InnerCreateCourseRequest.builder()
                                       .authorId(userId)
                                       .courseDescription(createCourseRequest.getCourseDescription())
                                       .courseName(createCourseRequest.getCourseName())
                                       .build();
    }

    /**
     * Формирует внутренний запрос на создание модуля.
     * @param userId идентификатор автора модуля
     * @param createModuleRequest входной запрос с названием и описанием модуля
     * @return собранный запрос для вызова внешнего сервиса
     * @throws NullPointerException в случае, если запрос равен {@code null}
     */
    public InnerCreateModuleRequest mapToInnerCreateModuleRequest(
            Long userId,
            CreateModuleRequest createModuleRequest) {
        return InnerCreateModuleRequest.builder()
                                       .userId(userId)
                                       .courseId(createModuleRequest.getCourseId())
                                       .moduleTitle(createModuleRequest.getModuleTitle())
                                       .moduleOrderNumber(createModuleRequest.getModuleOrderNumber())
                                       .moduleContent(createModuleRequest.getModuleContentDescription())
                                       .build();

    }

}
