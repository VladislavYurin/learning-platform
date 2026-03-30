package ru.mentor.services.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mentor.constant.MdcKeys;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.dto.front.CourseAccessRequest;
import ru.mentor.dto.front.ModuleAccessRequest;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.MentorClient;
import ru.mentor.mapper.AccessMapper;
import ru.mentor.services.RedirectAccessService;
import ru.mentor.services.UserService;

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
        Long userId = user.getId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");

        log.debug(
                "[userId={}] [targetUserId={}] [courseId={}] Получен запрос на добавление доступа к курсу.",
                userId,
                request.getUserId(),
                request.getCourseId()
        );

        GetAccessRequest innerRequest = accessMapper.mapToGetAccessRequest(user, request);

        try {
            ResponseEntity<?> response = mentorClient.giveCourseAccess(requestId, innerRequest);

            log.debug(
                    "[userId={}] [targetUserId={}] [courseId={}] Успешно получен ответ от mentor-service на добавление доступа к курсу.",
                    userId,
                    request.getUserId(),
                    request.getCourseId()
            );

            return response;
        } catch (Exception e) {
            log.error(
                    "[userId={}] [targetUserId={}] [courseId={}] Ошибка при вызове mentor-service во время добавления доступа к курсу.",
                    userId,
                    request.getUserId(),
                    request.getCourseId(),
                    e
            );
            throw e;
        }
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
        Long userId = user.getId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");

        log.debug(
                "[userId={}] [targetUserId={}] [courseId={}] Получен запрос на удаление доступа к курсу.",
                userId,
                request.getUserId(),
                request.getCourseId()
        );

        GetAccessRequest innerRequest = accessMapper.mapToGetAccessRequest(user, request);

        try {
            ResponseEntity<?> response = mentorClient.revokeCourseAccess(requestId, innerRequest);

            log.debug(
                    "[userId={}] [targetUserId={}] [courseId={}] Успешно получен ответ от mentor-service на удаление доступа к курсу.",
                    userId,
                    request.getUserId(),
                    request.getCourseId()
            );

            return response;
        } catch (Exception e) {
            log.error(
                    "[userId={}] [targetUserId={}] [courseId={}] Ошибка при вызове mentor-service во время удаления доступа к курсу.",
                    userId,
                    request.getUserId(),
                    request.getCourseId(),
                    e
            );
            throw e;
        }
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
        Long userId = user.getId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");

        log.debug(
                "[userId={}] [targetUserId={}] [moduleId={}] Получен запрос на добавление доступа к модулю.",
                userId,
                request.getUserId(),
                request.getModuleId()
        );

        GetAccessRequest innerRequest = accessMapper.mapToGetAccessRequest(user, request);

        try {
            ResponseEntity<?> response = mentorClient.giveModuleAccess(requestId, innerRequest);

            log.debug(
                    "[userId={}] [targetUserId={}] [moduleId={}] Успешно получен ответ от mentor-service на добавление доступа к модулю.",
                    userId,
                    request.getUserId(),
                    request.getModuleId()
            );

            return response;
        } catch (Exception e) {
            log.error(
                    "[userId={}] [targetUserId={}] [moduleId={}] Ошибка при вызове mentor-service во время добавления доступа к модулю.",
                    userId,
                    request.getUserId(),
                    request.getModuleId(),
                    e
            );
            throw e;
        }
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
        Long userId = user.getId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");

        log.debug(
                "[userId={}] [targetUserId={}] [moduleId={}] Получен запрос на удаление доступа к модулю.",
                userId,
                request.getUserId(),
                request.getModuleId()
        );

        GetAccessRequest innerRequest = accessMapper.mapToGetAccessRequest(user, request);

        try {
            ResponseEntity<?> response = mentorClient.revokeModuleAccess(requestId, innerRequest);

            log.debug(
                    "[userId={}] [targetUserId={}] [moduleId={}] Успешно получен ответ от mentor-service на удаление доступа к модулю.",
                    userId,
                    request.getUserId(),
                    request.getModuleId()
            );

            return response;
        } catch (Exception e) {
            log.error(
                    "[userId={}] [targetUserId={}] [moduleId={}] Ошибка при вызове mentor-service во время удаления доступа к модулю.",
                    userId,
                    request.getUserId(),
                    request.getModuleId(),
                    e
            );
            throw e;
        }
    }
}