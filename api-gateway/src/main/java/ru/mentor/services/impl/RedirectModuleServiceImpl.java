package ru.mentor.services.impl;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.DeleteModuleRequest;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.Header;
import ru.mentor.common.ImportModuleFromFileRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.UpdateModuleGrpcRequest;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;
import ru.mentor.dto.front.UpdateModuleRequest;
import ru.mentor.exception.GrpcExceptionMapper;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.CourseServiceModuleGrpcClient;
import ru.mentor.mapper.ModuleMapper;
import ru.mentor.services.RedirectModuleService;
import ru.mentor.services.UserService;
import ru.mentor.util.RqGenerator;

import java.io.IOException;

/**
 * Реализация сервиса редиректов/интеграции для операций с модулями курса.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectModuleServiceImpl implements RedirectModuleService {

    private final UserService userService;

    private final CourseServiceModuleGrpcClient moduleGrpcClient;

    private final ModuleMapper moduleMapper;

    private final HeaderFactory headerFactory;

    private final GrpcExceptionMapper exceptionMapper;

    /**
     * Создаёт gRPC-запрос для создания нового модуля в составе курса и передает gRPC-клиенту
     * для передачи серверу.
     *
     * @param request
     *         ДТО запроса для создания модуля
     *
     * @return ДТО созданного модуля
     */
    @Override
    public ModuleDto createModule(CreateModuleRequest request) {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ requestId = {} ] Получен запрос на создание модуля в курсе [ ID = {} ]"
                        + " юзером [ ID = {} ].", requestId, request.getCourseId(), userId
        );
        CreateModuleGrpcRequest createModuleRequest = moduleMapper.constructGrpcCreateRequest(
                header, userId, request);
        try {
            ModuleResponse moduleResponse = moduleGrpcClient.createModule(createModuleRequest);
            return moduleMapper.mapGrpcModuleResponseToModuleDto(moduleResponse);
        } catch (StatusRuntimeException e) {
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создаёт gRPC-запрос для получения модуля курса по порядковому номеру модуля и
     * передает gRPC-клиенту для передачи серверу
     *
     * @param courseId
     *         идентификатор курса
     * @param moduleId
     *         идентификатор модуля курса
     *
     * @return найденный модуль
     */
    @Override
    public ModuleDto getModuleById(Long courseId, Long moduleId) {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ requestId = {} ] Получен запрос на получение модуля [ ID = {} ] из курса "
                        + "[ ID = {} ] юзером [ ID = {} ].", requestId, moduleId,
                courseId, userId
        );

        GetModuleRequest request = moduleMapper.constructGrpcGetRequest(
                header,
                userId,
                courseId,
                moduleId
        );
        try {
            ModuleResponse moduleResponse = moduleGrpcClient.getModule(request);
            return moduleMapper.mapGrpcModuleResponseToModuleDto(moduleResponse);
        } catch (StatusRuntimeException e) {
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для импорта модуля из загруженного файла (например, Markdown).
     *
     * @param request
     *         ДТО запроса с параметрами создаваемого модуля
     * @param file
     *         файл с содержимым модуля
     *
     * @return созданный модуль
     */
    @Override
    public ModuleDto importModuleFromFile(CreateModuleRequest request, MultipartFile file) {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ requestId = {} ] Получен запрос на импорт файлом модуля в курсе [ ID = {} ] "
                        + "юзером [ ID = {} ].", requestId, request.getCourseId(), userId
        );
        try {
            ImportModuleFromFileRequest importModuleFromFileRequest = moduleMapper.constructGrpcImportFromFileRequest(
                    header,
                    userId,
                    request,
                    file
            );

            ModuleResponse moduleResponse = moduleGrpcClient.importModuleFromMarkdown(
                    importModuleFromFileRequest);
            return moduleMapper.mapGrpcModuleResponseToModuleDto(moduleResponse);
        } catch (StatusRuntimeException e) {
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Создаёт gRPC-запрос для обновления модуля в составе курса и передает gRPC-клиенту
     * для передачи серверу.
     *
     * @param request
     *         ДТО запроса для обновления модуля
     *
     * @return ДТО обновленного модуля
     */
    @Override
    public ModuleDto updateModule(UpdateModuleRequest request) {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ requestId = {} ] Получен запрос на обновление модуля [ ID = {} ] в курсе [ ID = {} ] юзером [ ID = {} ].",
                requestId, request.getModuleId(), request.getCourseId(), userId
        );

        UpdateModuleGrpcRequest updateRequest =
                moduleMapper.constructGrpcUpdateRequest(header, userId, request);

        try {
            ModuleResponse moduleResponse = moduleGrpcClient.updateModule(updateRequest);
            return moduleMapper.mapGrpcModuleResponseToModuleDto(moduleResponse);
        } catch (StatusRuntimeException e) {
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для удаления модуля из курса и передает его gRPC-клиенту для отправки
     * серверу.
     *
     * @param courseId
     *         идентификатор курса, из которого нужно удалить модуль
     * @param moduleId
     *         идентификатор модуля в курсе
     */
    @Override
    public void deleteModule(Long courseId, Long moduleId) {
        Long userId = userService.getCurrentUserId();
        String requestId = RqGenerator.generateRqId();
        Header header = headerFactory.create(requestId);
        log.info(
                "[ RqUId = {} ] Получен запрос на удаление модуля [ ID = {} ] в курсе [ ID = {} ] "
                        + "от юзера [ ID = {} ].",
                requestId,
                moduleId,
                courseId,
                userId
        );
        DeleteModuleRequest request = moduleMapper.constructGrpcDeleteRequest(
                header, userId, courseId, moduleId);
        try {
            moduleGrpcClient.deleteModule(request);
        } catch (StatusRuntimeException e) {
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

}
