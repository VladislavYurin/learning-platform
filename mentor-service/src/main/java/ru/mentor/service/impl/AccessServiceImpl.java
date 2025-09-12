package ru.mentor.service.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserCourseAccessEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.entity.UserModuleAccessEntity;
import ru.mentor.exception.CustomAccessDeniedException;
import ru.mentor.exception.EntityAlreadyExistsException;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.kafka.KafkaFacade;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.repository.UserCourseAccessRepository;
import ru.mentor.repository.UserModuleAccessRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.AccessService;
import ru.mentor.util.AccessChecker;

/**
 * Реализация сервиса управления доступом к курсам и модулям.
 * Предоставляет методы для предоставления и удаления доступа пользователей
 * к образовательным ресурсам в зависимости от их ролей.
 */
@Service
@RequiredArgsConstructor
public class AccessServiceImpl implements AccessService {

    private final CourseRepository courseRepository;

    private final ModuleRepository moduleRepository;

    private final UserRepository userRepository;

    private final AccessChecker accessChecker;

    private final UserCourseAccessRepository userCourseAccessRepository;

    private final UserModuleAccessRepository userModuleAccessRepository;

    private final KafkaFacade kafkaFacade;

    /**
     * Предоставляет пользователю доступ к курсу.
     *
     * @param rqUId Идентификатор запроса, ассоциированный с текущей сессией.
     * @param request Запрос, содержащий идентификаторы наставника, пользователя и курса.
     * @throws EntityAlreadyExistsException Если у пользователя уже имеется доступ к курсу.
     */
    @Override
    public void getCourseAccessToUser(String rqUId, GetAccessRequest request) {
        UserEntity mentor = userRepository.findByIdOrThrow(request.getMentorId());
        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());
        checkUserIsAuthorOrAdmin(rqUId, mentor, course);

        // Проверяем, имеет ли пользователь доступ к курсу
        if (!accessChecker.hasAccessToCourse(user.getId(), course.getId())) {
            UserCourseAccessEntity access = UserCourseAccessEntity.builder()
                                                                  .user(user)
                                                                  .course(course)
                                                                  .accessGrantedBy(mentor)
                                                                  .build();
            UserCourseAccessEntity savedAccess = userCourseAccessRepository.save(access);
            kafkaFacade.sendCourseAccessGrantedMessage(user, mentor, course, savedAccess);
        } else {
            // Если пользователь уже имеет доступ к курсу, выбрасываем исключение
            throw new EntityAlreadyExistsException(String.format(
                    "Юзер с ID = %d уже имеет доступ к курсу %d",
                    user.getId(),
                    course.getId()
            ), rqUId);
        }
    }

    /**
     * Предоставляет пользователю доступ к модулю.
     *
     * @param rqUId Идентификатор запроса, ассоциированный с текущей сессией.
     * @param request Запрос, содержащий идентификаторы наставника, пользователя, курса и модуля.
     * @throws EntityAlreadyExistsException Если у пользователя уже имеется доступ к модулю.
     * @throws EntityNotFoundException Если у пользователя нет доступа к курсу.
     */
    @Override
    public void getModuleAccessToUser(String rqUId, GetAccessRequest request) {
        UserEntity mentor = userRepository.findByIdOrThrow(request.getMentorId());
        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());
        ModuleEntity module = moduleRepository.findByIdOrThrow(request.getModuleId());
        checkUserIsAuthorOrAdmin(rqUId, mentor, course);
        checkModuleIsInCourse(rqUId, course, module);

        // Проверяем, имеет ли пользователь доступ к модулю
        if (accessChecker.hasAccessToModule(user.getId(), module.getId())) {
            throw new EntityAlreadyExistsException(String.format(
                    "Юзер с ID = %d уже имеет доступ к модулю %d",
                    user.getId(),
                    module.getId()
            ), rqUId);
        }

        // Проверяем, имеет ли пользователь доступ к курсу
        if (!accessChecker.hasAccessToCourse(user.getId(), course.getId())) {
            throw new EntityNotFoundException(String.format(
                    "Юзер с ID = %d не имеет доступа к курсу %d",
                    user.getId(),
                    course.getId()
            ), rqUId);
        }

        UserModuleAccessEntity access = UserModuleAccessEntity.builder()
                                                              .user(user)
                                                              .course(course)
                                                              .module(module)
                                                              .accessGrantedBy(mentor)
                                                              .build();
        UserModuleAccessEntity savedAccess = userModuleAccessRepository.save(access);
        kafkaFacade.sendModuleAccessGrantedMessage(user, mentor, course, module, savedAccess);

    }

    /**
     * Удаляет доступ пользователя к курсу.
     *
     * @param rqUId Идентификатор запроса, ассоциированный с текущей сессией.
     * @param request Запрос, содержащий идентификаторы наставника, пользователя и курса.
     * @throws CustomAccessDeniedException Если наставник не имеет прав на удаление доступа к курсу.
     */
    @Override
    public void deleteCourseAccessToUser(String rqUId, GetAccessRequest request) {
        UserEntity mentor = userRepository.findByIdOrThrow(request.getMentorId());
        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());
        checkUserIsAuthorOrAdmin(rqUId, mentor, course);
        accessChecker.hasAccessToCourse(request.getUserId(), request.getCourseId());
        
        // Получаем запись о доступе перед удалением, чтобы получить дату отзыва
        UserCourseAccessEntity access = userCourseAccessRepository
                .findByUserIdAndCourseId(request.getUserId(), request.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Доступ пользователя %d к курсу %d не найден", 
                                request.getUserId(), request.getCourseId()),
                        rqUId));
        
        LocalDateTime accessRevokedAt = LocalDateTime.now();
        
        // Удаляем доступ к курсу и всем его модулям
        userCourseAccessRepository.deleteByUserIdAndCourseId(
                request.getUserId(),
                request.getCourseId()
        );
        userModuleAccessRepository.deleteAllByUserIdAndCourseId(
                request.getUserId(),
                request.getCourseId()
        );
        
        // Отправляем уведомление об отзыве доступа к курсу
        kafkaFacade.sendCourseAccessRevokedMessage(user, mentor, course, accessRevokedAt);
    }

    /**
     * Удаляет доступ пользователя к модулю.
     *
     * @param rqUId Идентификатор запроса, ассоциированный с текущей сессией.
     * @param request Запрос, содержащий идентификаторы наставника, пользователя, курса и модуля.
     * @throws CustomAccessDeniedException Если наставник не имеет прав на удаление доступа к модулю.
     * @throws EntityNotFoundException Если у пользователя нет доступа к курсу или модулю.
     */
    @Override
    public void deleteModuleAccessToUser(String rqUId, GetAccessRequest request) {
        UserEntity mentor = userRepository.findByIdOrThrow(request.getMentorId());
        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());
        ModuleEntity module = moduleRepository.findByIdOrThrow(request.getModuleId());
        checkUserIsAuthorOrAdmin(rqUId, mentor, course);
        checkModuleIsInCourse(rqUId, course, module);
        accessChecker.hasAccessToCourse(request.getUserId(), request.getCourseId());
        accessChecker.hasAccessToModule(request.getUserId(), request.getModuleId());
        
        // Проверяем существование доступа перед удалением
        if (!userModuleAccessRepository.existsByUserIdAndModuleId(request.getUserId(), request.getModuleId())) {
            throw new EntityNotFoundException(
                    String.format("Доступ пользователя %d к модулю %d не найден", 
                            request.getUserId(), request.getModuleId()),
                    rqUId);
        }
        
        LocalDateTime accessRevokedAt = LocalDateTime.now();
        
        // Удаляем доступ к модулю
        userModuleAccessRepository.deleteByUserIdAndModuleId(
                request.getUserId(),
                request.getModuleId()
        );
        
        // Отправляем уведомление об отзыве доступа к модулю
        kafkaFacade.sendModuleAccessRevokedMessage(user, mentor, course, module, accessRevokedAt);
    }

    /**
     * Проверяет, что пользователь является автором курса или администратором.
     *
     * @param rqUId Идентификатор запроса, ассоциированный с текущей сессией.
     * @param mentor Наставник, запрашивающий доступ.
     * @param course Курс, для которого требуется доступ.
     * @throws CustomAccessDeniedException Если у наставника нет прав на выдачу доступа к курсу.
     */
    private void checkUserIsAuthorOrAdmin(String rqUId, UserEntity mentor, CourseEntity course) {

        // Проверяем, что юзер является автором курса
        if (Role.checkIsAdmin(mentor)) {
            return;
        }

        // Проверяем, что юзер является автором курса
        if (Role.checkIsMentor(mentor) && Role.checkMentorIsAuthorOfCourse(mentor, course)) {
            return;
        }

        // Если юзер не является автором курса, выбрасываем исключение
        throw new CustomAccessDeniedException(String.format(
                "Юзер с ID = %d не имеет доступа к выдаче доступа к курсу %d",
                mentor.getId(),
                course.getId()
        ), rqUId);
    }

    /**
     * Проверяет, что модуль принадлежит указанному курсу.
     *
     * @param rqUId Идентификатор запроса, ассоциированный с текущей сессией.
     * @param course Курс, к которому принадлежит модуль.
     * @param moduleEntity Модуль для проверки.
     * @throws EntityNotFoundException Если модуль не принадлежит курсу.
     */
    private void checkModuleIsInCourse(
            String rqUId,
            CourseEntity course,
            ModuleEntity moduleEntity) {

        // Проверяем, что модуль принадлежит курсу
        if (Objects.equals(moduleEntity.getCourse().getId(), course.getId())) {
            return;
        }
        // Если модуль не принадлежит курсу, выбрасываем исключение
        throw new EntityNotFoundException(
                String.format(
                        "Модуль с ID = %d не принадлежит курсу с ID = %d",
                        moduleEntity.getId(),
                        course.getId()
                ), rqUId);
    }

}
