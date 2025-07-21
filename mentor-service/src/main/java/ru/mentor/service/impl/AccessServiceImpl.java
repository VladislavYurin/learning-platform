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
import ru.mentor.exception.AccessDeniedException;
import ru.mentor.exception.EntityAlreadyExistsException;
import ru.mentor.exception.EntityNotFoundException;
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

    @Override
    public void getCourseAccessToUser(GetAccessRequest request) {
        UserEntity mentor = userRepository.findByIdOrThrow(request.getMentorId());
        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());
        checkUserIsAuthorOrAdmin(mentor, course);
        if (!accessChecker.hasAccessToCourse(user.getId(), course.getId())) {
            UserCourseAccessEntity access = UserCourseAccessEntity.builder()
                                                                  .user(user)
                                                                  .course(course)
                                                                  .build();
            userCourseAccessRepository.save(access);
        } else {
            throw new EntityAlreadyExistsException(String.format(
                    "Юзер с ID = %d уже имеет доступ к курсу %d",
                    user.getId(),
                    course.getId()
            ));
        }
    }

    @Override
    public void getModuleAccessToUser(GetAccessRequest request) {
        UserEntity mentor = userRepository.findByIdOrThrow(request.getMentorId());
        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());
        ModuleEntity module = moduleRepository.findByIdOrThrow(request.getModuleId());
        checkUserIsAuthorOrAdmin(mentor, course);
        checkModuleIsInCourse(course, module);
        if (!accessChecker.hasAccessToModule(user.getId(), course.getId())) {
            UserModuleAccessEntity access = UserModuleAccessEntity.builder()
                                                                  .user(user)
                                                                  .course(course)
                                                                  .module(module)
                                                                  .build();
            userModuleAccessRepository.save(access);
        } else {
            throw new EntityAlreadyExistsException(String.format(
                    "Юзер с ID = %d уже имеет доступ к модулю %d",
                    user.getId(),
                    module.getId()
            ));
        }

    }

    private void checkUserIsAuthorOrAdmin(UserEntity mentor, CourseEntity course) {
        if (Role.checkIsAdmin(mentor)) {
            return;
        }

        if (Role.checkIsMentor(mentor) && course.getAuthor().equals(mentor)) {
            return;
        }

        throw new AccessDeniedException(String.format(
                "Юзер с ID = %d не имеет доступа к выдаче доступа к курсу %d",
                mentor.getId(),
                course.getId()
        ));
    }

    private void checkModuleIsInCourse(CourseEntity course, ModuleEntity moduleEntity) {
        if (Objects.equals(moduleEntity.getCourse().getId(), course.getId())) {
            return;
        }
        throw new EntityNotFoundException(
                String.format(
                        "Модуль с ID = %d не принадлежит курсу с ID = %d",
                        moduleEntity.getId(),
                        course.getId()
                )
        );
    }

}
