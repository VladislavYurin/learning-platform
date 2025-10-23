package ru.mentor.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.CourseDtoWithoutModules;
import ru.mentor.dto.InnerCreateCourseRequest;
import ru.mentor.dto.ModuleDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.CourseTagLinkEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserCourseAccessEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.entity.UserModuleAccessEntity;
import ru.mentor.exception.CustomAccessDeniedException;
import ru.mentor.kafka.KafkaFacade;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.CourseTagRepository;
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
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final BaseMapper baseMapper;

    private final AccessChecker accessChecker;

    private final UserCourseAccessRepository userCourseAccessRepository;

    private final UserModuleAccessRepository userModuleAccessRepository;

    private final KafkaFacade kafkaFacade;

    private final CourseTagRepository tagRepository;

    /**
     * Создает новый курс от имени пользователя (ментора или администратора).
     *
     * @param request Запрос на создание курса, содержащий данные о названии, описании и ID автора курса.
     * @return DTO курса, созданного в результате операции, содержащий информацию о курсе.
     * @throws CustomAccessDeniedException Если пользователь не обладает необходимыми правами для создания курса.
     */
    @Override
    @Transactional
    public CourseDto createCourse(InnerCreateCourseRequest request) {
        UserEntity user = userRepository.findByIdOrThrow(request.getAuthorId());

        CourseEntity course = CourseEntity.builder()
                                          .author(user)
                                          .courseTitle(request.getCourseName())
                                          .description(request.getCourseDescription())
                                          .build();

        List<Long> ids = getCourseTagIds(request);

        course.getCourseTags().addAll(
                ids.stream()
                   .map(id -> CourseTagLinkEntity.builder()
                                                 .course(course)
                                                 .tag(tagRepository.getReferenceById(id))
                                                 .createdAt(LocalDateTime.now())
                                                 .build())
                   .toList()
        );
        CourseEntity courseEntity = courseRepository.save(course);
        return baseMapper.mapCourse(courseEntity, user, false, false, true);
    }

    /**
     * Удаление курса
     *
     * @param userId
     *         идентификатор пользователя, выполняющего операцию
     * @param courseId
     *         идентификатор удаляемого курса
     *
     * @throws CustomAccessDeniedException
     *         исключение если пользователю запрещен доступ к данной операции
     */
    @Override
    @Transactional
    public void deleteCourse(Long userId, Long courseId) {
        UserEntity deletedByUser = userRepository.findByIdOrThrow(userId);
        CourseEntity course = courseRepository.findByIdOrThrow(courseId);

        // Админ может удалять любой курс
        if (Role.checkIsAdmin(deletedByUser)) {
            courseRepository.deleteById(courseId);
            kafkaFacade.sendCourseDeletedMessage(course, deletedByUser);
            return;
        }

        // Ментор может удалять только свои курсы
        if (Role.checkIsMentor(deletedByUser) && course.getAuthor().equals(deletedByUser)) {
            courseRepository.delete(course);
            kafkaFacade.sendCourseDeletedMessage(course, deletedByUser);
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
     * Получение списка активных курсов
     *
     * @param userId
     *         идентификатор пользователя, выполняющего операцию
     *
     * @return List<CourseDto> список активных курсов
     */
    @Override
    public List<CourseDto> getAllActiveCourses(Long userId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        if (Role.checkIsAdmin(user)) {
            List<CourseEntity> courseEntities = courseRepository.findAllByIsActiveTrue();
            return baseMapper.mapCourses(courseEntities, false, false, true);
        }
        if (Role.checkIsMentor(user)) {
            List<CourseEntity> courseEntities = courseRepository.findAllByIsActiveTrueAndAuthorId(
                    userId);
            return baseMapper.mapCourses(courseEntities, false, false, true);
        }
        List<UserCourseAccessEntity> userCourseAccessEntities = user.getCourseAccesses();
        if (userCourseAccessEntities.isEmpty()) {
            return null;
        }
        List<CourseEntity> courses = userCourseAccessEntities.stream()
                                                             .map(UserCourseAccessEntity::getCourse)
                                                             .filter(CourseEntity::getIsActive)
                                                             .toList();
        return baseMapper.mapCourses(courses, false, false, true);
    }

    /**
     * Получение всех курсов
     *
     * @param userId
     *         идентификатор пользователя, который хочет получить курсы
     *
     * @return List<CourseDto> список курсов
     */
    @Override
    public List<CourseDto> getAllCourses(Long userId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        if (Role.checkIsAdmin(user)) {
            List<CourseEntity> courseEntities = courseRepository.findAll();
            return baseMapper.mapCourses(courseEntities, false, false, true);
        }
        if (Role.checkIsMentor(user)) {
            List<CourseEntity> courseEntities = courseRepository.findAllByAuthorId(userId);
            return baseMapper.mapCourses(courseEntities, false, false, true);
        }
        List<UserCourseAccessEntity> userCourseAccessEntities = user.getCourseAccesses();
        if (userCourseAccessEntities.isEmpty()) {
            return Collections.emptyList();
        }
        List<CourseEntity> courses = userCourseAccessEntities.stream()
                                                             .map(UserCourseAccessEntity::getCourse)
                                                             .toList();
        return baseMapper.mapCourses(courses, false, false, true);
    }

    /**
     * Получение курса по идентификатору
     *
     * @param userId
     *         идентификатор пользователя, который хочет получить курсы
     * @param courseId
     *         идентификатор курса
     *
     * @return CourseDto ДТО курса полученная по идентификатору
     *
     * @throws CustomAccessDeniedException
     *         исключение если пользователю запрещен доступ к данной операции
     */
    @Override
    public CourseDto getCourseById(Long userId, Long courseId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        CourseEntity course = courseRepository.findByIdOrThrow(courseId);
        if (Role.checkIsAdmin(user) ||
                Role.checkIsMentor(user) && Role.checkMentorIsAuthorOfCourse(user, course)) {
            return baseMapper.mapCourse(course, course.getAuthor(), true, false, true);
        } else if (accessChecker.hasAccessToCourse(userId, courseId)) {
            List<UserModuleAccessEntity> userModuleAccessEntities = userModuleAccessRepository.findAllByUserIdAndCourseId(
                    userId,
                    courseId
            );
            List<ModuleEntity> moduleEntities = userModuleAccessEntities.stream().map(
                    UserModuleAccessEntity::getModule).toList();
            CourseDto courseDto = baseMapper.mapCourse(
                    course,
                    course.getAuthor(),
                    false,
                    false,
                    true
            );
            List<ModuleDto> modules = baseMapper.mapModules(moduleEntities, false);
            courseDto.setModules(modules);
            return courseDto;
        }
        throw new CustomAccessDeniedException(
                String.format(
                        "Юзер с ID = %d не имеет доступа к курсу с ID = %d",
                        userId,
                        courseId
                )
        );
    }

    private List<Long> getCourseTagIds(InnerCreateCourseRequest request) {
        return request.getTagIds() == null ? List.of()
                : request.getTagIds().stream().distinct().toList();
    }

    /**
     * Получение списка всех активных курсов (без модулей) с информацией о наставнике
     *
     * @return список активных курсов (без модулей) с информацией о наставнике
     */
    @Override
    public List<CourseDtoWithoutModules> getAllActiveCoursesPreview() {
        List<CourseEntity> courseEntityList = courseRepository.findAllByIsActiveTrue()
                .stream()
                .toList();
            return baseMapper.mapCoursesWithoutModules(courseEntityList, false);
        }
}