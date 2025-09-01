package ru.mentor.services;

import org.springframework.http.ResponseEntity;
import ru.mentor.dto.front.CourseAccessRequest;
import ru.mentor.dto.front.ModuleAccessRequest;

/**
 * Сервис редиректов/интеграции для операций выдачи/отзыва доступа к курсам и модулям.
 * <p>
 *     Инкапсулирует обращение к внешнему сервису доступа и скрывает детали транспорта
 *     (заголовки, авторизация, коды ответов). Используется контроллерами уровня API.
 * </p>
 */
public interface RedirectAccessService {

    /**
     * Предоставляет пользователю доступ к курсу.
     * @param request параметры предоставления доступа (идентификаторы пользователя и курса)
     * @return ответ внешнего сервиса с кодом статуса операции
     */
    ResponseEntity<?> giveCourseAccess(CourseAccessRequest request);

    /**
     * Закрывает доступ пользователя к курсу.
     * @param request параметры предоставления доступа (идентификаторы пользователя и курса)
     * @return ответ внешнего сервиса с кодом статуса операции
     */
    ResponseEntity<?> revokeCourseAccess(CourseAccessRequest request);

    /**
     * Предоставляет пользователю доступ к модулю.
     * @param request параметры предоставления доступа (идентификаторы пользователя, курса и модуля)
     * @return ответ внешнего сервиса с кодом статуса операции
     */
    ResponseEntity<?> giveModuleAccess(ModuleAccessRequest request);

    /**
     * Закрывает доступ пользователя к модулю.
     * @param request параметры предоставления доступа (идентификаторы пользователя, курса и модуля)
     * @return ответ внешнего сервиса с кодом статуса операции
     */
    ResponseEntity<?> revokeModuleAccess(ModuleAccessRequest request);

}
