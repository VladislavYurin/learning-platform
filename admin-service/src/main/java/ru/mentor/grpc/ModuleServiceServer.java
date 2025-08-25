package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.mentor.admin.AdminModuleServiceGrpc.AdminModuleServiceImplBase;
import ru.mentor.admin.AllModulesResponse;
import ru.mentor.admin.GetModuleRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.ModuleResponse;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.ModuleRepository;

/**
 * gRPC-сервис для работы с модулями для админов
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ModuleServiceServer extends AdminModuleServiceImplBase {

    private final ModuleRepository moduleRepository;

    private final AdminModuleMapper moduleMapper;

    private final BaseMapper baseMapper;

    /**
     * Возвращает модуль по ID
     *
     * @param request
     *         gRPC-объект {@link GetModuleRequest} запроса страницы
     * @param responseObserver
     *         объект для отправки ответа
     */
    @Override
    public void getModule(
            GetModuleRequest request,
            StreamObserver<ModuleResponse> responseObserver) {

        String requestId = request.getRequestId();
        long moduleId = request.getModuleId();
        log.info(
                "Поступил запрос [ ID = {} ] на получение данных о модуле [ ID = {} ] от администратора",
                requestId,
                moduleId
        );

        try {
            ModuleEntity courseEntity = moduleRepository.findByIdOrThrow(moduleId);
            ModuleResponse moduleResponse =
                    moduleMapper.mapModuleEntityToModuleResponse(courseEntity);

            responseObserver.onNext(moduleResponse);
            responseObserver.onCompleted();

        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        }

    }

    /**
     * Возвращает gRPC-объект, содержащий список модулей.
     *
     * @param request
     *         gRPC-объект {@link GetModuleRequest} запроса страницы
     * @param responseObserver
     *         объект для отправки ответа
     */
    @Override
    public void getAllModules(
            GrpcPageRequest request,
            StreamObserver<AllModulesResponse> responseObserver) {

        String requestId = request.getRequestId();
        log.info(
                "Поступил запрос [ ID = {} ] на получение данных обо всех модулях от администратора",
                requestId
        );

        try {
            PageRequest pageRequest = baseMapper.mapGrpcPageRequestToPageRequest(request);
            Page<ModuleEntity> moduleEntityPage = moduleRepository.findAll(pageRequest);

            AllModulesResponse allCoursesResponse =
                    moduleMapper.mapModuleEntityPageToGrpcAllModulesResponse(moduleEntityPage);

            responseObserver.onNext(allCoursesResponse);
            responseObserver.onCompleted();

        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        }

    }

}
