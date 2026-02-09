package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.dto.front.CourseAccessRequest;
import ru.mentor.dto.front.ModuleAccessRequest;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.MentorClient;
import ru.mentor.mapper.AccessMapper;
import ru.mentor.services.RedirectAccessService;
import ru.mentor.services.UserService;
import ru.mentor.util.RqGenerator;

/**
 * Реализация сервиса редиректов/интеграции для операций выдачи/отзыва доступа к курсам и модулям.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectAccessServiceImpl implements RedirectAccessService {

    private final UserService userService;

    private final AccessMapper accessMapper;

    private final MentorClient mentorClient;

    /**
     * Предоставляет пользователю доступ к курсу.
     *
     * @param request
     *         параметры предоставления доступа (идентификаторы пользователя и курса)
     *
     * @return ответ внешнего сервиса с кодом статуса операции
     */
    @Override
    public ResponseEntity<?> giveCourseAccess(CourseAccessRequest request) {
        UserEntity user = userService.getCurrentUser();
        String requestId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на добавление доступа юзеру [ ID = %d ] к курсу [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                request.getUserId(),
                request.getCourseId(),
                user.getId()
        ));
        GetAccessRequest innerRequest = accessMapper.toGetAccessRequest(user, request);
        return mentorClient.giveCourseAccess(requestId, innerRequest);
    }

    /**
     * Закрывает доступ пользователя к курсу.
     *
     * @param request
     *         параметры предоставления доступа (идентификаторы пользователя и курса)
     *
     * @return ответ внешнего сервиса с кодом статуса операции
     */
    @Override
    public ResponseEntity<?> revokeCourseAccess(CourseAccessRequest request) {
        UserEntity user = userService.getCurrentUser();
        String requestId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на удаление доступа юзеру [ ID = %d ] к курсу [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                request.getUserId(),
                request.getCourseId(),
                user.getId()
        ));
        GetAccessRequest innerRequest = accessMapper.toGetAccessRequest(user, request);
        return mentorClient.revokeCourseAccess(requestId, innerRequest);
    }

    /**
     * Предоставляет пользователю доступ к модулю.
     *
     * @param request
     *         параметры предоставления доступа (идентификаторы пользователя, курса и модуля)
     *
     * @return ответ внешнего сервиса с кодом статуса операции
     */
    @Override
    public ResponseEntity<?> giveModuleAccess(ModuleAccessRequest request) {
        UserEntity user = userService.getCurrentUser();
        String requestId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на добавление доступа юзеру [ ID = %d ] к модулю [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                request.getUserId(),
                request.getModuleId(),
                user.getId()
        ));
        GetAccessRequest innerRequest = accessMapper.toGetAccessRequest(user, request);
        return mentorClient.giveModuleAccess(requestId, innerRequest);
    }

    /**
     * Закрывает доступ пользователя к модулю.
     *
     * @param request
     *         параметры предоставления доступа (идентификаторы пользователя, курса и модуля)
     *
     * @return ответ внешнего сервиса с кодом статуса операции
     */
    @Override
    public ResponseEntity<?> revokeModuleAccess(ModuleAccessRequest request) {
        UserEntity user = userService.getCurrentUser();
        String requestId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на удаление доступа юзеру [ ID = %d ] к модулю [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                request.getUserId(),
                request.getModuleId(),
                user.getId()
        ));
        GetAccessRequest innerRequest = accessMapper.toGetAccessRequest(user, request);
        return mentorClient.revokeModuleAccess(requestId, innerRequest);
    }

}
