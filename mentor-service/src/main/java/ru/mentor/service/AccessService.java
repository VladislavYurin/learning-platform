package ru.mentor.service;

import ru.mentor.dto.GetAccessRequest;

/**
 * Интерфейс для управления доступом к курсам и модулям.
 * Предоставляет методы для предоставления и удаления доступа
 * пользователей к образовательным ресурсам.
 */
public interface AccessService {

    void getCourseAccessToUser(String rqUId, GetAccessRequest request);

    void getModuleAccessToUser(String rqUId, GetAccessRequest request);

    void deleteCourseAccessToUser(String rqUId, GetAccessRequest request);

    void deleteModuleAccessToUser(String rqUId, GetAccessRequest request);

}
