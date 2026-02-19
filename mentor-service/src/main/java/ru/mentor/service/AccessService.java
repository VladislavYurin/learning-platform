package ru.mentor.service;

import reactor.core.publisher.Mono;
import ru.mentor.dto.*;


/**
 * Интерфейс для управления доступом к курсам и модулям.
 * Предоставляет методы для предоставления и удаления доступа
 * пользователей к образовательным ресурсам.
 */
public interface AccessService {

    Mono<Void> grantCourseAccess(String requestId, GrantCourseAccessRequestDto request);

    Mono<Void> grantModuleAccessToUser(String requestId, GrantModuleAccessRequest request);

    Mono<Void> revokeCourseAccessFromUser(String requestId, RevokeCourseAccessRequest request);

    Mono<Void> revokeModuleAccessFromUser(String requestId, RevokeModuleAccessRequest request);

}
