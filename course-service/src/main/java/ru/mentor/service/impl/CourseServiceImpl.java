package ru.mentor.service.impl;

import io.grpc.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.mentor.common.AllActiveCoursesResponse;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.DeleteCourseResponse;
import ru.mentor.common.GetAllActiveCoursesPreviewRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.constant.Role;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.facade.CourseFacade;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.repository.UserModuleAccessRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.CourseService;
import ru.mentor.util.AccessChecker;

/**
 * Реализация сервиса для управления курсами.
 * Управляет доступом к методам фасада в соответствии с ролями пользователей.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseFacade courseFacade;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final AccessChecker accessChecker;
    private final ModuleRepository moduleRepository;
    private final UserModuleAccessRepository userModuleAccessRepository;

    /**
     * Находит в базе данных пользователя по ID из запроса, после чего проверяет его роль.
     * Если роль позволяет, вызывает метод фасада для создания курса и передает в него данные
     * запроса.
     *
     * @param request gRPC-запрос с данными для создания курса
     *
     * @return Mono с gRPC-ответом, содержащим данные созданного курса
     */
    @Override
    public Mono<CourseResponse> createCourse(CreateCourseGrpcRequest request) {
        return userRepository
                .findByIdOrThrow(request.getUserId())
                .flatMap(user -> {
                    if (Role.checkIsMentor(user) || Role.checkIsAdmin(user)) {
                        return courseFacade.createCourse(request, user);
                    } else {
                        return Mono.error(
                                Status.PERMISSION_DENIED
                                        .withDescription(String.format(
                                                "Юзер с [ ID = %d ] не имеет доступа к созданию курса",
                                                user.getId()
                                        ))
                                        .asRuntimeException()
                        );
                    }
                });
    }

    /**
     * Находит в базе данных пользователя, находит курс по ID из запроса и, если
     * у пользователя подходящая роль, находит в базе данных курс по ID из запроса,
     * проверяет права пользователя на курс. Если права есть, вызывает метод фасада
     * для удаления курса.
     *
     * @param request - gRPC запрос {@link DeleteCourseRequest} с данными для удаления курса
     *
     * @return - пустой gRPC - ответ
     */
    @Override
    public Mono<DeleteCourseResponse> deleteCourse(DeleteCourseRequest request) {
        return accessChecker
                .isCourseAuthor(request.getSenderId(), request.getCourseId())
                .flatMap(isAuthor -> {
                    if (isAuthor) {
                        return courseFacade.deleteCourse(request.getCourseId());
                    } else {
                        return Mono.error(
                                Status.PERMISSION_DENIED
                                        .withDescription(String.format(
                                                "Юзер с [ ID = %d ] не имеет доступа к удалению"
                                                        + " курса [ ID = %d ]",
                                                request.getSenderId(),
                                                request.getCourseId()
                                        ))
                                        .asRuntimeException()
                        );
                    }
                });
    }

    /**
     * Вызывает метод фасада для получения данных обо всех курсах
     *
     * @param request - параметры пагинации
     *
     * @return Mono с gRPC-ответом, содержащим список курсов
     */
    @Override
    public Mono<AllCoursesResponse> getAllCourses(GrpcPageRequest request) {
        return courseFacade.findAllCourses(request);
    }

    /**
     * Вызывает метод фасада для получения данных всех активных курсов
     *
     * @param request - параметры пагинации
     *
     * @return Mono с gRPC-ответом, содержащим список курсов
     */

    @Override
    public Mono<AllCoursesResponse> getAllActiveCourses(GrpcPageRequest request) {
        return courseFacade.findAllActiveCourses(request);
    }

    /**
     * Вызывает метод фасада для получения данных обо всех активных курсах
     * без информации о модулях
     *
     * @param request - gRPC запрос превью всех активных курсов
     *
     * @return Mono с gRPC-ответом, содержащим список курсов
     */
    @Override
    public Mono<AllActiveCoursesResponse> getAllActiveCoursesPreview(GetAllActiveCoursesPreviewRequest request) {
        return courseFacade.findAllActiveCoursesPreview(request)
                           .map(list ->
                               AllActiveCoursesResponse.newBuilder()
                                                       .addAllCourses(list)
                                                       .build());
    }

    /**
     * Находит в базе данных пользователя по ID из запроса, находит в базе курс по ID из запроса,
     * проверяет, есть ли у пользователя доступ к курсу и к модулям. Если доступ есть,
     * находит в базе список модулей и вызывает метод фасада для получения информации о курсе.
     *
     * @param request - gRPC - запрос данных о курсе
     *
     * @return Mono с gRPC-ответом, содержащим данные о курсе, модулях, тегах
     */
    @Override
    public Mono<CourseResponse> getCourseById(GetCourseRequest request) {
        return userRepository
            .findByIdOrThrow(request.getSenderId())
            .flatMap(user ->
                courseRepository
                    .findByIdOrThrow(request.getCourseId())
                    .flatMap(course ->
                        accessChecker.hasAccessToCourse(user.getId(), course.getId())
                            .flatMap(hasAccess -> {
                                Mono<List<ModuleEntity>> modules;
                                if (Role.checkIsMentor(user) &&
                                    user.getId().equals(course.getAuthorId())) {
                                        modules = moduleRepository.findAllByCourseId(course.getId())
                                                                  .collectList();
                                } else if (hasAccess) {
                                    modules = userModuleAccessRepository
                                            .findAllByUserIdAndCourseId(user.getId(), course.getId())
                                            .flatMap(link ->
                                                moduleRepository.findById(link.getModuleId()))
                                            .collectList();
                                } else {
                                    return Mono.error(
                                        Status.PERMISSION_DENIED
                                              .withDescription(String.format(
                                                  "Юзер с [ ID = %d ] не имеет доступа к курсу с [ ID = %d ]",
                                                  user.getId(),
                                                  request.getCourseId()
                                              ))
                                              .asRuntimeException()
                                    );
                                }
                                return userRepository.findByIdOrThrow(course.getAuthorId())
                                    .flatMap(author ->
                                        modules.flatMap(modulesList ->
                                            courseFacade.getCourse(course, author, modulesList)
                                        )
                                    );
                            })
                    )
                );
    }
}