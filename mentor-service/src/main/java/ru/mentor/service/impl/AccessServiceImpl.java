package ru.mentor.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.mentor.AccessChecker;


import ru.mentor.dto.*;
import ru.mentor.entity.*;
import ru.mentor.exception.EntityAlreadyExistsException;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.grpc.MentorAccessValidator;
import ru.mentor.repository.*;
import ru.mentor.service.AccessService;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Реализация сервиса управления доступом к курсам и модулям.
 * Предоставляет методы для предоставления и удаления доступа пользователей
 * к образовательным ресурсам в зависимости от их ролей.
 */
@Service
@RequiredArgsConstructor
public class AccessServiceImpl implements AccessService {

    //private final CourseFacade courseFacade; переделать на методы с ним
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    ModuleRepository moduleRepository;
    private final AccessChecker accessChecker;
    private final MentorAccessValidator mentorAccessValidator;
    //добавить Kafka
    //kafkaFacade.sendCourseAccessGrantedMessage() должен вернуть Mono<Void> добавить в reactive-common-lib
    private final UserCourseAccessRepository userCourseAccessRepository;
    private final UserModuleAccessRepository userModuleAccessRepository;

    /**
     * Предоставляет пользователю доступ к курсу.
     *
     * @param requestId Идентификатор запроса, ассоциированный с текущей сессией.
     * @param request   Запрос, содержащий идентификаторы наставника, пользователя и курса.
     * @throws EntityAlreadyExistsException Если у пользователя уже имеется доступ к курсу.
     */
    @Override
    public Mono<Void> grantCourseAccess(String requestId, GrantCourseAccessRequestDto request) {

        Mono<UserEntity> mentorMono = getUserOrError(request.getMentorId(), requestId);
        Mono<UserEntity> userMono = getUserOrError(request.getUserId(), requestId);
        Mono<CourseEntity> courseMono = getCourseOrError(request.getCourseId(), requestId);

        return Mono.zip(mentorMono, userMono, courseMono)
                .flatMap(tuple -> {
                    UserEntity mentor = tuple.getT1();
                    UserEntity user = tuple.getT2();
                    CourseEntity course = tuple.getT3();

                    // Проверяем, является пользователь автором курса или администратором
                    return mentorAccessValidator.checkUserIsAuthorOrAdmin(requestId, mentor, course)

                            //Проверяем, имеет ли пользователь доступ к курсу
                            .then(checkUserHasNoAccessToCourse(user, course, requestId))
                            .then(grantAccess(user, course, mentor)
                                    // сюда добавить отправку в кафку
                            )
                            .then();
                });
    }

    private Mono<UserEntity> getUserOrError(Long id, String requestId) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        String.format(
                                "Пользователь с ID = %d не найден",
                                id
                        ),
                        requestId)));
    }

    private Mono<CourseEntity> getCourseOrError(Long id, String requestId) {
        return courseRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        String.format(
                                "Курс с ID = %d не найден",
                                id),
                        requestId)));
    }

    private Mono<Void> checkUserHasNoAccessToCourse(UserEntity user, CourseEntity course, String requestId) {
        return accessChecker.hasAccessToCourse(user.getId(), course.getId())
                .flatMap(hasAccess -> {
                    if (hasAccess) {
                        return Mono.error(new EntityAlreadyExistsException(
                                String.format(
                                        "Юзер с ID = %d уже имеет доступ к курсу %d",
                                        user.getId(),
                                        course.getId()
                                ),
                                requestId
                        ));
                    }
                    return Mono.empty();
                });
    }

    private Mono<UserCourseAccessEntity> grantAccess(
            UserEntity user,
            CourseEntity course,
            UserEntity mentor
    ) {
        UserCourseAccessEntity access = UserCourseAccessEntity.builder()
                .userId(user.getId())
                .courseId(course.getId())
                .accessGrantedByUserId(mentor.getId())
                .build();
        return userCourseAccessRepository.save(access);
    }

    /**
     * Предоставляет пользователю доступ к модулю.
     *
     * @param requestId Идентификатор запроса, ассоциированный с текущей сессией.
     * @param request   Запрос, содержащий идентификаторы наставника, пользователя, курса и модуля.
     * @throws EntityAlreadyExistsException Если у пользователя уже имеется доступ к модулю.
     * @throws EntityNotFoundException      Если у пользователя нет доступа к курсу.
     */
    @Override
    public Mono<Void> grantModuleAccessToUser(String requestId, GrantModuleAccessRequest request) {
        Mono<UserEntity> mentorMono = getUserOrError(request.getMentorId(), requestId);
        Mono<UserEntity> userMono = getUserOrError(request.getUserId(), requestId);
        Mono<CourseEntity> courseMono = getCourseOrError(request.getCourseId(), requestId);
        Mono<ModuleEntity> moduleMono = getModuleOrError(request.getModuleId(), requestId);

        return Mono.zip(mentorMono, userMono, courseMono, moduleMono)
                .flatMap(tuple -> {
                    UserEntity mentor = tuple.getT1();
                    UserEntity user = tuple.getT2();
                    CourseEntity course = tuple.getT3();
                    ModuleEntity module = tuple.getT4();

                    // Проверяем, является пользователь автором курса или администратором
                    return mentorAccessValidator.checkUserIsAuthorOrAdmin(requestId, mentor, course)
                            // Проверяем, имеет ли пользователь доступ к модулю
                            .then(checkUserHasNoAccessToModule(user, module, requestId))
                            //Удостоверяемся, что пользователь имеет доступ к курсу
                            .then(checkUserHasAccessToCourse(user, course, requestId))
                            //Даем доступ к модулю
                            .then(grantAccessToModule(user, course, module, mentor)
                                    // сюда добавить отправку в кафку
                            ).then();
                });

    }

    private Mono<ModuleEntity> getModuleOrError(Long moduleId, String requestId) {
        return moduleRepository.findById(moduleId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                                String.format(
                                        "Модуль с ID = %d не найден",
                                        moduleId
                                ),
                                requestId
                        )
                ));
    }

    private Mono<UserModuleAccessEntity> grantAccessToModule(
            UserEntity user,
            CourseEntity course,
            ModuleEntity module,
            UserEntity mentor
    ) {
        UserModuleAccessEntity access = UserModuleAccessEntity.builder()
                .userId(user.getId())
                .courseId(course.getId())
                .moduleId(module.getId())
                .accessGrantedByUserId(mentor.getId())
                .build();
        return userModuleAccessRepository.save(access);
    }

    private Mono<Void> checkUserHasAccessToCourse(
            UserEntity user,
            CourseEntity course,
            String requestId
    ) {
        return accessChecker.hasAccessToCourse(user.getId(), course.getId())
                .flatMap(hasAccess -> {
                    if (!hasAccess) {
                        return Mono.error(new EntityNotFoundException(
                                String.format(
                                        "Юзер с ID = %d не имеет доступа к курсу %d",
                                        user.getId(),
                                        course.getId()
                                ),
                                requestId
                        ));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> checkUserHasNoAccessToModule(UserEntity user, ModuleEntity module, String requestId) {
        return accessChecker.hasAccessToModule(user.getId(), module.getId())
                .flatMap(hasAccess -> {
                    if (hasAccess) {
                        return Mono.error(new EntityAlreadyExistsException(
                                String.format(
                                        "Пользователь с ID = %d уже имеет доступ к модулю %d",
                                        user.getId(),
                                        module.getId()
                                ),
                                requestId
                        ));
                    }
                    return Mono.empty();
                });
    }

    /**
     * Удаляет доступ пользователя к курсу.
     *
     * @param requestId Идентификатор запроса, ассоциированный с текущей сессией.
     * @param request   Запрос, содержащий идентификаторы наставника, пользователя и курса.
     * @throws //CustomAccessDeniedException Если наставник не имеет прав на удаление доступа к курсу.
     */
    @Override
    @Transactional
    public Mono<Void> revokeCourseAccessFromUser(String requestId, RevokeCourseAccessRequest request) {
        Mono<UserEntity> mentorMono = getUserOrError(request.getMentorId(), requestId);
        Mono<UserEntity> userMono = getUserOrError(request.getUserId(), requestId);
        Mono<CourseEntity> courseMono = getCourseOrError(request.getCourseId(), requestId);

        return Mono.zip(mentorMono, userMono, courseMono)
                .flatMap(tuple -> {
                    UserEntity mentor = tuple.getT1();
                    UserEntity user = tuple.getT2();
                    CourseEntity course = tuple.getT3();
                    LocalDateTime accessRevokedAt = LocalDateTime.now();

                    // Проверяем, является пользователь автором курса или администратором
                    return mentorAccessValidator.checkUserIsAuthorOrAdmin(requestId, mentor, course)
                            .then(checkUserHasAccessToCourse(user, course, requestId))
                            .then(revokeAccess(user, course))
                            .then();
                });
    }


    private Mono<Void> revokeAccess(UserEntity user, CourseEntity course) {
        return userCourseAccessRepository
                .deleteByUserIdAndCourseId(user.getId(), course.getId())
                .then(
                        userModuleAccessRepository
                                .deleteAllByUserIdAndCourseId(user.getId(), course.getId())
                                .then()
                );
    }

    /**
     * Удаляет доступ пользователя к модулю.
     *
     * @param requestId Идентификатор запроса, ассоциированный с текущей сессией.
     * @param request   Запрос, содержащий идентификаторы наставника, пользователя, курса и модуля.
     * @throws //CustomAccessDeniedException Если наставник не имеет прав на удаление доступа к модулю.
     * @throws EntityNotFoundException       Если у пользователя нет доступа к курсу или модулю.
     */
    @Override
    public Mono<Void> revokeModuleAccessFromUser(String requestId, RevokeModuleAccessRequest request) {
        Mono<UserEntity> mentorMono = getUserOrError(request.getMentorId(), requestId);
        Mono<UserEntity> userMono = getUserOrError(request.getUserId(), requestId);
        Mono<CourseEntity> courseMono = getCourseOrError(request.getCourseId(), requestId);
        Mono<ModuleEntity> moduleMono = getModuleOrError(request.getModuleId(), requestId);

        return Mono.zip(mentorMono, userMono, courseMono, moduleMono)
                .flatMap(tuple -> {
                    UserEntity mentor = tuple.getT1();
                    UserEntity user = tuple.getT2();
                    CourseEntity course = tuple.getT3();
                    ModuleEntity module = tuple.getT4();
                    LocalDateTime accessRevokedAt = LocalDateTime.now();

                    return mentorAccessValidator.checkUserIsAuthorOrAdmin(requestId, mentor, course)
                            // проверить принадлежность модуля к курсу
                            .then(checkModuleIsInCourse(requestId, course, module))
                            //проверить доступ к курсу,
                            .then(checkUserHasAccessToCourse(user, course, requestId))
                            // проверить доступ пользователя к модулю
                            .then(checkUserHasAccessToModule(user, course, module, requestId))

                            // далее найти сущность доступа в репозитории и удалить ее
                            .then(userModuleAccessRepository.deleteAllByUserIdAndCourseId(
                                    user.getId(),
                                    course.getId()
                            ))


                            .then();
                });
    }

    private Mono<Void> checkUserHasAccessToModule(
            UserEntity user,
            CourseEntity course,
            ModuleEntity module,
            String requestId
    ) {
        return accessChecker.hasAccessToModule(user.getId(), module.getId())
                .flatMap(hasAccess -> {
                    if (!hasAccess) {
                        return Mono.error(new EntityNotFoundException(
                                String.format(
                                        "Пользователь с ID = %d не имеет доступа к модулю %d в курсе %d",
                                        user.getId(),
                                        module.getId(),
                                        course.getId()
                                ),
                                requestId
                        ));
                    }
                    return Mono.empty();
                });
    }


    /*
    @Override
    @Transactional
    public void deleteModuleAccessToUser(String requestId, GetAccessRequest request) {
        UserEntity mentor = userRepository.findByIdOrThrow(request.getMentorId());
        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());
        ModuleEntity module = moduleRepository.findByIdOrThrow(request.getModuleId());
        checkUserIsAuthorOrAdmin(requestId, mentor, course);
        checkModuleIsInCourse(requestId, course, module);
        accessChecker.hasAccessToCourse(request.getUserId(), request.getCourseId());
        accessChecker.hasAccessToModule(request.getUserId(), request.getModuleId());

        if (!userModuleAccessRepository.existsByUserIdAndModuleId(
                request.getUserId(),
                request.getModuleId()
        )) {
            throw new EntityNotFoundException(
                    String.format(
                            "Доступ пользователя %d к модулю %d не найден",
                            request.getUserId(), request.getModuleId()
                    ),
                    requestId
            );
        }

        LocalDateTime accessRevokedAt = LocalDateTime.now();

        userModuleAccessRepository.deleteByUserIdAndModuleId(
                request.getUserId(),
                request.getModuleId()
        );

        kafkaFacade.sendModuleAccessRevokedMessage(user, mentor, course, module, accessRevokedAt);
    }

*/

    /**
     * Проверяет, что модуль принадлежит указанному курсу.
     *
     * @param requestId    Идентификатор запроса, ассоциированный с текущей сессией.
     * @param course       Курс, к которому принадлежит модуль.
     * @param moduleEntity Модуль для проверки.
     * @throws EntityNotFoundException Если модуль не принадлежит курсу.
     */
    private Mono<Void> checkModuleIsInCourse(
            String requestId,
            CourseEntity course,
            ModuleEntity moduleEntity) {

        // Проверяем, что модуль принадлежит курсу
        if (Objects.equals(moduleEntity.getCourseId(), course.getId())) {
            return Mono.empty();
        }
        // Если модуль не принадлежит курсу, выбрасываем исключение
        throw new EntityNotFoundException(
                String.format(
                        "Модуль с ID = %d не принадлежит курсу с ID = %d",
                        moduleEntity.getId(),
                        course.getId()
                ), requestId
        );
    }

}
