package ru.mentor.service.impl;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.InnerCreateCourseRequest;
import ru.mentor.dto.ModuleDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserCourseAccessEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.entity.UserModuleAccessEntity;
import ru.mentor.exception.AccessDeniedException;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.UserCourseAccessRepository;
import ru.mentor.repository.UserModuleAccessRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.CourseService;
import ru.mentor.util.AccessChecker;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final BaseMapper baseMapper;

    private final AccessChecker accessChecker;

    private final UserCourseAccessRepository userCourseAccessRepository;

    private final UserModuleAccessRepository userModuleAccessRepository;

    @Override
    public CourseDto createCourse(InnerCreateCourseRequest request) {
        UserEntity user = userRepository.findByIdOrThrow(request.getAuthorId());
        if (Role.checkIsMentor(user) || Role.checkIsAdmin(user)) {
            CourseEntity course = new CourseEntity();
            course.setAuthor(user);
            course.setCourseTitle(request.getCourseName());
            course.setDescription(request.getCourseDescription());
            CourseEntity courseEntity = courseRepository.save(course);
            return baseMapper.mapCourse(courseEntity, user, false, false);
        } else {
            throw new AccessDeniedException(
                    String.format(
                            "Юзер с ID = %d не имеет доступа к созданию курса",
                            request.getAuthorId()
                    )
            );
        }
    }

    @Override
    public void deleteCourse(Long userId, Long courseId) {
        UserEntity deletedByUser = userRepository.findByIdOrThrow(userId);
        CourseEntity course = courseRepository.findByIdOrThrow(courseId);

        // Админ может удалять любой курс
        if (Role.checkIsAdmin(deletedByUser)) {
            courseRepository.deleteById(courseId);
            return;
        }

        // Ментор может удалять только свои курсы
        if (Role.checkIsMentor(deletedByUser) && course.getAuthor().equals(deletedByUser)) {
            courseRepository.delete(course);
            return;
        }

        // Если дошли сюда — доступ запрещён
        throw new AccessDeniedException(
                String.format(
                        "Юзер с ID = %d не имеет доступа к удалению курса с ID = %d",
                        userId,
                        courseId
                )
        );
    }

    @Override
    public List<CourseDto> getAllActiveCourses(Long userId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        if (Role.checkIsAdmin(user)) {
            List<CourseEntity> courseEntities = courseRepository.findAllByIsActiveTrue();
            return baseMapper.mapCourses(courseEntities, false, false);
        }
        if (Role.checkIsMentor(user)) {
            List<CourseEntity> courseEntities = courseRepository.findAllByIsActiveTrueAndAuthorId(
                    userId);
            return baseMapper.mapCourses(courseEntities, false, false);
        }
        List<UserCourseAccessEntity> userCourseAccessEntities = user.getCourseAccesses();
        if (userCourseAccessEntities.isEmpty()) {
            return null;
        }
        List<CourseEntity> courses = userCourseAccessEntities.stream()
                                                             .map(UserCourseAccessEntity::getCourse)
                                                             .filter(CourseEntity::getIsActive)
                                                             .toList();
        return baseMapper.mapCourses(courses, false, false);
    }

    @Override
    public List<CourseDto> getAllCourses(Long userId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        if (Role.checkIsAdmin(user)) {
            List<CourseEntity> courseEntities = courseRepository.findAll();
            return baseMapper.mapCourses(courseEntities, false, false);
        }
        if (Role.checkIsMentor(user)) {
            List<CourseEntity> courseEntities = courseRepository.findAllByAuthorId(userId);
            return baseMapper.mapCourses(courseEntities, false, false);
        }
        List<UserCourseAccessEntity> userCourseAccessEntities = user.getCourseAccesses();
        if (userCourseAccessEntities.isEmpty()) {
            return Collections.emptyList();
        }
        List<CourseEntity> courses = userCourseAccessEntities.stream()
                                                             .map(UserCourseAccessEntity::getCourse)
                                                             .toList();
        return baseMapper.mapCourses(courses, false, false);
    }

    @Override
    public CourseDto getCourseById(Long userId, Long courseId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        CourseEntity course = courseRepository.findByIdOrThrow(courseId);
        if (Role.checkIsAdmin(user) ||
                Role.checkIsMentor(user) && Role.checkMentorIsAuthorOfCourse(user, course)) {
            return baseMapper.mapCourse(course, course.getAuthor(), true, false);
        } else if (accessChecker.hasAccessToCourse(userId, courseId)) {
            List<UserModuleAccessEntity> userModuleAccessEntities = userModuleAccessRepository.findAllByUserIdAndCourseId(
                    userId,
                    courseId
            );
            List<ModuleEntity> moduleEntities = userModuleAccessEntities.stream().map(
                    UserModuleAccessEntity::getModule).toList();
            CourseDto courseDto = baseMapper.mapCourse(course, course.getAuthor(), false, false);
            List<ModuleDto> modules = baseMapper.mapModules(moduleEntities, false);
            courseDto.setModules(modules);
            return courseDto;
        }
        throw new AccessDeniedException(
                String.format(
                        "Юзер с ID = %d не имеет доступа к курсу с ID = %d",
                        userId,
                        courseId
                )
        );

    }

}
