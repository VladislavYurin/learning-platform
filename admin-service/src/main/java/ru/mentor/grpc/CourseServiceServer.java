package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.mentor.admin.AdminCourseServiceGrpc.AdminCourseServiceImplBase;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.GetCourseRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.entity.CourseEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;

/**
 * gRPC-сервис для работы с курсами для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CourseServiceServer extends AdminCourseServiceImplBase {

    private final CourseRepository courseRepository;

    private final AdminCourseMapper courseMapper;

    private final BaseMapper baseMapper;

    /**
     * Возвращает курс по ID
     *
     * @param request
     *         gRPC-объект {@link GrpcPageRequest} запроса страницы
     * @param responseObserver
     *         объект для возврата ответа
     */
    @Override
    public void getCourse(
            GetCourseRequest request,
            StreamObserver<CourseResponse> responseObserver) {

        String requestId = request.getRequestId();
        long courseId = request.getCourseId();
        log.info(
                "Поступил запрос [ ID = {} ] на получение данных о курсе [ ID = {} ] от администратора",
                requestId,
                courseId
        );

        try {
            CourseEntity courseEntity = courseRepository.findByIdOrThrow(courseId);
            CourseResponse courseResponse = courseMapper
                                                    .mapCourseEntityToGrpcCourseResponse(
                                                            courseEntity);

            responseObserver.onNext(courseResponse);
            responseObserver.onCompleted();

        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        }

    }

    @Override
    public void getAllCourses(
            GrpcPageRequest request,
            StreamObserver<AllCoursesResponse> responseObserver) {

        String requestId = request.getRequestId();
        log.info(
                "Поступил запрос [ ID = {} ] на получение данных обо всех курсах от администратора",
                requestId
        );

        try {
            PageRequest pageRequest = baseMapper.mapGrpcPageRequestToPageRequest(request);
            Page<CourseEntity> courseEntity = courseRepository.findAll(pageRequest);

            AllCoursesResponse allCoursesResponse = courseMapper
                                                            .mapCourseEntityPageToGrpcAllCoursesResponse(
                                                                    courseEntity);

            responseObserver.onNext(allCoursesResponse);
            responseObserver.onCompleted();

        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        }
    }

}
