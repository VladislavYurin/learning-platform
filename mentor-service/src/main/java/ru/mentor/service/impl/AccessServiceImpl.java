package ru.mentor.service.impl;

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

    @Override
    public void getCourseAccessToUser(String rqUId, GetAccessRequest request) {
        UserEntity mentor = userRepository.findByIdOrThrow(request.getMentorId());
        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());
        checkUserIsAuthorOrAdmin(rqUId, mentor, course);
        if (!accessChecker.hasAccessToCourse(user.getId(), course.getId())) {
            UserCourseAccessEntity access = UserCourseAccessEntity.builder()
                                                                  .user(user)
                                                                  .course(course)
                                                                  .accessGrantedBy(mentor)
                                                                  .build();
            UserCourseAccessEntity savedAccess = userCourseAccessRepository.save(access);
            kafkaFacade.sendCourseAccessGrantedMessage(user, mentor, course, savedAccess);
        } else {
            throw new EntityAlreadyExistsException(String.format(
                    "Юзер с ID = %d уже имеет доступ к курсу %d",
                    user.getId(),
                    course.getId()
            ), rqUId);
        }
    }

    @Override
    public void getModuleAccessToUser(String rqUId, GetAccessRequest request) {
        UserEntity mentor = userRepository.findByIdOrThrow(request.getMentorId());
        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());
        ModuleEntity module = moduleRepository.findByIdOrThrow(request.getModuleId());
        checkUserIsAuthorOrAdmin(rqUId, mentor, course);
        checkModuleIsInCourse(rqUId, course, module);

        if (accessChecker.hasAccessToModule(user.getId(), module.getId())) {
            throw new EntityAlreadyExistsException(String.format(
                    "Юзер с ID = %d уже имеет доступ к модулю %d",
                    user.getId(),
                    module.getId()
            ), rqUId);
        }

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

    @Override
    public void deleteCourseAccessToUser(String rqUId, GetAccessRequest request) {
        UserEntity mentor = userRepository.findByIdOrThrow(request.getMentorId());
        userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());
        checkUserIsAuthorOrAdmin(rqUId, mentor, course);
        accessChecker.hasAccessToCourse(request.getUserId(), request.getCourseId());
        userCourseAccessRepository.deleteByUserIdAndCourseId(
                request.getUserId(),
                request.getCourseId()
        );
        userModuleAccessRepository.deleteAllByUserIdAndCourseId(
                request.getUserId(),
                request.getCourseId()
        );
    }

    @Override
    public void deleteModuleAccessToUser(String rqUId, GetAccessRequest request) {
        UserEntity mentor = userRepository.findByIdOrThrow(request.getMentorId());
        userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());
        ModuleEntity module = moduleRepository.findByIdOrThrow(request.getModuleId());
        checkUserIsAuthorOrAdmin(rqUId, mentor, course);
        checkModuleIsInCourse(rqUId, course, module);
        accessChecker.hasAccessToCourse(request.getUserId(), request.getCourseId());
        accessChecker.hasAccessToModule(request.getUserId(), request.getModuleId());
        userModuleAccessRepository.deleteByUserIdAndModuleId(
                request.getUserId(),
                request.getModuleId()
        );
    }

    private void checkUserIsAuthorOrAdmin(String rqUId, UserEntity mentor, CourseEntity course) {
        if (Role.checkIsAdmin(mentor)) {
            return;
        }

        if (Role.checkIsMentor(mentor) && Role.checkMentorIsAuthorOfCourse(mentor, course)) {
            return;
        }

        throw new CustomAccessDeniedException(String.format(
                "Юзер с ID = %d не имеет доступа к выдаче доступа к курсу %d",
                mentor.getId(),
                course.getId()
        ), rqUId);
    }

    private void checkModuleIsInCourse(
            String rqUId,
            CourseEntity course,
            ModuleEntity moduleEntity) {
        if (Objects.equals(moduleEntity.getCourse().getId(), course.getId())) {
            return;
        }
        throw new EntityNotFoundException(
                String.format(
                        "Модуль с ID = %d не принадлежит курсу с ID = %d",
                        moduleEntity.getId(),
                        course.getId()
                ), rqUId);
    }

}
