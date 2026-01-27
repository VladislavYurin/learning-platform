package ru.mentor.service;

import reactor.core.publisher.Mono;


/**
 * Интерфейс для управления доступом к курсам и модулям.
 * Предоставляет методы для предоставления и удаления доступа
 * пользователей к образовательным ресурсам.
 */
public interface AccessService {

    Mono<Void> getCourseAccessToUser(String requestId, CourseAccessRequest request);

    Mono<Void> getModuleAccessToUser(String requestId, ModuleAccessRequest request);

    Mono<Void> deleteCourseAccessToUser(String requestId, CourseAccessRequest request);

    Mono<Void> deleteModuleAccessToUser(String requestId, ModuleAccessRequest request);

}
