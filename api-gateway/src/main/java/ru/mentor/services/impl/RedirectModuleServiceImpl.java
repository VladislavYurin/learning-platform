package ru.mentor.services.impl;

import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.DeleteModuleRequest;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.Header;
import ru.mentor.common.ImportModuleFromFileRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.constant.MdcKeys;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;
import ru.mentor.exception.GrpcExceptionMapper;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.CourseServiceModuleGrpcClient;
import ru.mentor.mapper.ModuleMapper;
import ru.mentor.services.RedirectModuleService;
import ru.mentor.services.UserService;

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
     * @param request ДТО запроса для создания модуля
     * @return ДТО созданного модуля
     */
    @Override
    public ModuleDto createModule(CreateModuleRequest request) {
        Long userId = userService.getCurrentUserId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] [courseId={}] Запрос на создание модуля.",
                userId,
                request.getCourseId()
        );

        CreateModuleGrpcRequest createModuleRequest =
                moduleMapper.constructGrpcCreateRequest(header, userId, request);

        try {
            ModuleResponse moduleResponse = moduleGrpcClient.createModule(createModuleRequest);

            log.debug(
                    "[userId={}] [courseId={}] Модуль успешно создан.",
                    userId,
                    request.getCourseId()
            );

            return moduleMapper.mapGrpcModuleResponseToModuleDto(moduleResponse);
        } catch (StatusRuntimeException e) {
            logGrpcError(
                    "создания модуля",
                    userId,
                    e,
                    "[courseId=%s]",
                    request.getCourseId()
            );
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создаёт gRPC-запрос для получения модуля курса по порядковому номеру модуля.
     *
     * @param courseId идентификатор курса
     * @param moduleId идентификатор модуля курса
     * @return найденный модуль
     */
    @Override
    public ModuleDto getModuleById(Long courseId, Long moduleId) {
        Long userId = userService.getCurrentUserId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] [courseId={}] [moduleId={}] Запрос на получение модуля.",
                userId,
                courseId,
                moduleId
        );

        GetModuleRequest request = moduleMapper.constructGrpcGetRequest(
                header,
                userId,
                courseId,
                moduleId
        );

        try {
            ModuleResponse moduleResponse = moduleGrpcClient.getModule(request);

            log.debug(
                    "[userId={}] [courseId={}] [moduleId={}] Модуль успешно получен.",
                    userId,
                    courseId,
                    moduleId
            );

            return moduleMapper.mapGrpcModuleResponseToModuleDto(moduleResponse);
        } catch (StatusRuntimeException e) {
            logGrpcError(
                    "получения модуля",
                    userId,
                    e,
                    "[courseId=%s] [moduleId=%s]",
                    courseId,
                    moduleId
            );
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Создает gRPC-запрос для импорта модуля из файла.
     *
     * @param request ДТО запроса
     * @param file файл с содержимым модуля
     * @return созданный модуль
     */
    @Override
    public ModuleDto importModuleFromFile(CreateModuleRequest request, MultipartFile file) {
        Long userId = userService.getCurrentUserId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] [courseId={}] [fileName={}] Запрос на импорт модуля из файла.",
                userId,
                request.getCourseId(),
                file.getOriginalFilename()
        );

        try {
            ImportModuleFromFileRequest importRequest =
                    moduleMapper.constructGrpcImportFromFileRequest(header, userId, request, file);

            ModuleResponse moduleResponse =
                    moduleGrpcClient.importModuleFromMarkdown(importRequest);

            log.debug(
                    "[userId={}] [courseId={}] [fileName={}] Модуль успешно импортирован из файла.",
                    userId,
                    request.getCourseId(),
                    file.getOriginalFilename()
            );

            return moduleMapper.mapGrpcModuleResponseToModuleDto(moduleResponse);
        } catch (StatusRuntimeException e) {
            logGrpcError(
                    "импорта модуля из файла",
                    userId,
                    e,
                    "[courseId=%s] [fileName=%s]",
                    request.getCourseId(),
                    file.getOriginalFilename()
            );
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        } catch (IOException e) {
            log.error(
                    "[userId={}] [courseId={}] [fileName={}] Ошибка чтения файла при импорте модуля.",
                    userId,
                    request.getCourseId(),
                    file.getOriginalFilename(),
                    e
            );
            throw new RuntimeException(e);
        }
    }

    /**
     * Создает gRPC-запрос для удаления модуля.
     *
     * @param courseId идентификатор курса
     * @param moduleId идентификатор модуля
     */
    @Override
    public void deleteModule(Long courseId, Long moduleId) {
        Long userId = userService.getCurrentUserId();
        String requestId = Optional.ofNullable(MDC.get(MdcKeys.REQUEST_ID)).orElse("");
        Header header = headerFactory.create(requestId);

        log.debug(
                "[userId={}] [courseId={}] [moduleId={}] Запрос на удаление модуля.",
                userId,
                courseId,
                moduleId
        );

        DeleteModuleRequest request =
                moduleMapper.constructGrpcDeleteRequest(header, userId, courseId, moduleId);

        try {
            moduleGrpcClient.deleteModule(request);

            log.debug(
                    "[userId={}] [courseId={}] [moduleId={}] Модуль успешно удалён.",
                    userId,
                    courseId,
                    moduleId
            );
        } catch (StatusRuntimeException e) {
            logGrpcError(
                    "удаления модуля",
                    userId,
                    e,
                    "[courseId=%s] [moduleId=%s]",
                    courseId,
                    moduleId
            );
            throw exceptionMapper.mapGrpcExceptionToRuntimeException(e, requestId);
        }
    }

    /**
     * Логирует ошибку gRPC-вызова с указанием статуса, описания и контекста операции.
     *
     * @param operation описание операции
     * @param userId идентификатор пользователя
     * @param e исключение gRPC
     * @param contextPattern шаблон контекста
     * @param contextArgs значения контекста
     */
    private void logGrpcError(
            String operation,
            Long userId,
            StatusRuntimeException e,
            String contextPattern,
            Object... contextArgs
    ) {
        String grpcDescription = Optional.ofNullable(e.getStatus().getDescription())
                .orElse("отсутствует");

        String context = String.format(contextPattern, contextArgs);

        log.error(
                "[userId={}] [operation={}] {} Ошибка gRPC-вызова. [grpcStatusCode={}] [grpcDescription={}]",
                userId,
                operation,
                context,
                e.getStatus().getCode(),
                grpcDescription,
                e
        );
    }
}