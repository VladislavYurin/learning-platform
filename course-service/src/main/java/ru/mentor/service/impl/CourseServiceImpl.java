package ru.mentor.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
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
import ru.mentor.exception.CustomAccessDeniedException;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.UserCourseAccessRepository;
import ru.mentor.repository.UserModuleAccessRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.CourseService;
import ru.mentor.util.AccessChecker;

/**
 * Реализация сервиса для управления курсами в системе управления онлайн-курсами.
 * Cервис предоставляет методы для создания и удаления курсов,
 * а также управляет доступом к ним в соответствии с ролями пользователей.
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final BaseMapper baseMapper;

    private final AccessChecker accessChecker;

    private final UserCourseAccessRepository userCourseAccessRepository;

    private final UserModuleAccessRepository userModuleAccessRepository;

    /**
     * Создает новый курс от имени пользователя (ментора или администратора).
     *
     * @param request Запрос на создание курса, содержащий данные о названии, описании и ID автора курса.
     * @return DTO курса, созданного в результате операции, содержащий информацию о курсе.
     * @throws CustomAccessDeniedException Если пользователь не обладает необходимыми правами для создания курса.
     */
    @Override
    public CourseDto createCourse(InnerCreateCourseRequest request) {
        UserEntity user = userRepository.findByIdOrThrow(request.getAuthorId());

        // Проверяем, является ли пользователь админом или ментором
        if (Role.checkIsMentor(user) || Role.checkIsAdmin(user)) {
            CourseEntity course = new CourseEntity();
            course.setAuthor(user);
            course.setCourseTitle(request.getCourseName());
            course.setDescription(request.getCourseDescription());
            CourseEntity courseEntity = courseRepository.save(course);
            return baseMapper.mapCourse(courseEntity, user, false, false);
        } else {
            throw new CustomAccessDeniedException(
                    String.format(
                            "Юзер с ID = %d не имеет доступа к созданию курса",
                            request.getAuthorId()
                    )
            );
        }
    }

    /**
     * Удаляет курс по его идентификатору, если пользователь имеет на это права.
     *
     * @param userId Идентификатор пользователя, инициирующего удаление курса.
     * @param courseId Идентификатор удаляемого курса.
     * @throws CustomAccessDeniedException Если пользователь не имеет прав для удаления курса.
     */
    @Override
    public void deleteCourse(Long userId, Long courseId) {
        UserEntity deletedByUser = userRepository.findByIdOrThrow(userId);
        CourseEntity course = courseRepository.findByIdOrThrow(courseId);

        // Проверка на права доступа:
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
        throw new CustomAccessDeniedException(
                String.format(
                        "Юзер с ID = %d не имеет доступа к удалению курса с ID = %d",
                        userId,
                        courseId
                )
        );
    }

    /**
     * Получает список всех активных курсов, доступных для пользователя.
     *
     * @param userId Идентификатор пользователя, для которого нужно получить список курсов.
     * @return Список DTO курсов, доступных для пользователя.
     * @throws NoSuchElementException Если пользователь с указанным {@code userId} не существует.
     */
    @Override
    public List<CourseDto> getAllActiveCourses(Long userId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);

        // Админ может получить все активные курсы
        if (Role.checkIsAdmin(user)) {
            List<CourseEntity> courseEntities = courseRepository.findAllByIsActiveTrue();
            return baseMapper.mapCourses(courseEntities, false, false);
        }

        // Ментор может получить свои активные курсы
        if (Role.checkIsMentor(user)) {
            List<CourseEntity> courseEntities = courseRepository.findAllByIsActiveTrueAndAuthorId(
                    userId);
            return baseMapper.mapCourses(courseEntities, false, false);
        }

        // Получаем доступные курсы для обычного пользователя
        List<UserCourseAccessEntity> userCourseAccessEntities = user.getCourseAccesses();
        if (userCourseAccessEntities.isEmpty()) {
            return null;
        }
        // Фильтруем доступные активные курсы
        List<CourseEntity> courses = userCourseAccessEntities.stream()
                                                             .map(UserCourseAccessEntity::getCourse)
                                                             .filter(CourseEntity::getIsActive)
                                                             .toList();
        return baseMapper.mapCourses(courses, false, false);
    }

    /**
     * Получает список всех курсов, доступных для пользователя.
     *
     * @param userId Идентификатор пользователя, для которого нужно получить список курсов.
     * @return Список DTO курсов, доступных для пользователя.
     * @throws NoSuchElementException Если пользователь с указанным {@code userId} не существует.
     */
    @Override
    public List<CourseDto> getAllCourses(Long userId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);

        // Админ может получить все курсы
        if (Role.checkIsAdmin(user)) {
            List<CourseEntity> courseEntities = courseRepository.findAll();
            return baseMapper.mapCourses(courseEntities, false, false);
        }

        // Ментор может получить только свои курсы
        if (Role.checkIsMentor(user)) {
            List<CourseEntity> courseEntities = courseRepository.findAllByAuthorId(userId);
            return baseMapper.mapCourses(courseEntities, false, false);
        }

        // Получаем доступные курсы для обычного пользователя
        List<UserCourseAccessEntity> userCourseAccessEntities = user.getCourseAccesses();
        if (userCourseAccessEntities.isEmpty()) {
            return Collections.emptyList();
        }

        // Получаем все курсы, на которые пользователь имеет доступ
        List<CourseEntity> courses = userCourseAccessEntities.stream()
                                                             .map(UserCourseAccessEntity::getCourse)
                                                             .toList();
        return baseMapper.mapCourses(courses, false, false);
    }

    /**
     * Получает курс по его идентификатору, доступный для пользователя.
     *
     * @param userId Идентификатор пользователя, для которого нужно получить курс.
     * @param courseId Идентификатор курса, который нужно получить.
     * @return DTO курса, доступного для пользователя.
     * @throws NoSuchElementException Если пользователь с указанным {@code userId} не существует.
     * @throws CustomAccessDeniedException Если пользователь не имеет прав для просмотра курса.
     */
    @Override
    public CourseDto getCourseById(Long userId, Long courseId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        CourseEntity course = courseRepository.findByIdOrThrow(courseId);

        // Проверяем права доступа пользователя
        if (Role.checkIsAdmin(user) ||
                Role.checkIsMentor(user) && Role.checkMentorIsAuthorOfCourse(user, course)) {
            return baseMapper.mapCourse(course, course.getAuthor(), true, false);
        } else if (accessChecker.hasAccessToCourse(userId, courseId)) {
            List<UserModuleAccessEntity> userModuleAccessEntities = userModuleAccessRepository.findAllByUserIdAndCourseId(
                    userId,
                    courseId
            );

            // Получаем модули, доступные пользователю
            List<ModuleEntity> moduleEntities = userModuleAccessEntities.stream().map(
                    UserModuleAccessEntity::getModule).toList();
            CourseDto courseDto = baseMapper.mapCourse(course, course.getAuthor(), false, false);
            List<ModuleDto> modules = baseMapper.mapModules(moduleEntities, false);
            courseDto.setModules(modules);
            return courseDto;
        }

        // Если дошли сюда — доступ запрещён
        throw new CustomAccessDeniedException(
                String.format(
                        "Юзер с ID = %d не имеет доступа к курсу с ID = %d",
                        userId,
                        courseId
                )
        );

    }

}
