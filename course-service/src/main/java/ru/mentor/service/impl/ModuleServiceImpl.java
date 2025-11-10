package ru.mentor.service.impl;

import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.DeleteModuleRequest;
import ru.mentor.common.DeleteModuleResponse;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.ImportModuleFromFileRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.facade.ModuleFacade;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.service.ModuleService;
import ru.mentor.util.AccessChecker;

/**
 * Сервис для управления модулями курса
 * Предоставляет методы для создания, удаления и получения модулей,
 * а также управляет доступом к ним в соответствии с ролями пользователей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleServiceImpl implements ModuleService {

    private final AccessChecker accessChecker;
    private final ModuleFacade moduleFacade;
    private final ModuleRepository moduleRepository;

    /**
     * Возвращает модуль курса, если у пользователя есть к нему доступ
     *
     * @param request - gRPC запрос, содержит id курса и порядковый номер модуля
     * @return - Mono с gRPC-ответом, содержащим данные модуля
     */
    @Override
    public Mono<ModuleResponse> getModule(GetModuleRequest request) {
        return accessChecker
                .hasAccessToModule(request.getSenderId(), request.getCourseId(), request.getModuleOrderNumber())
                .flatMap(hasAccess -> {
                    if (hasAccess) {
                        return moduleFacade
                                .findModuleResponseByCourseIdAndModuleOrderNum(
                                    request.getCourseId(),
                                    request.getModuleOrderNumber());
                    } else {
                        return Mono.error(
                            Status.PERMISSION_DENIED
                                .withDescription(
                                    String.format(
                                        "Юзер с [ ID = %d ] не имеет доступа к модулю "
                                        + "[ num = %d ] курса [ ID = %d ]"
                                        + "или такого модуля не существует",
                                        request.getSenderId(),
                                        request.getModuleOrderNumber(),
                                        request.getCourseId()
                                    )).asRuntimeException());
                    }
                });
    }

    /**
     * Создает новый модуль курса, если пользователь имеет право создавать модули
     *
     * @param request - gRPC-запрос с данными создаваемого курса
     * @return - Mono с gRPC-ответом, содержащим данные созданного курса
     */
    @Override
    @Transactional
    public Mono<ModuleResponse> createModule(CreateModuleGrpcRequest request) {
        return accessChecker
            .isCourseAuthor(request.getSenderId(), request.getCourseId())
            .flatMap(isAuthor -> {
                if (isAuthor) {
                    return moduleRepository
                            .existsByCourseIdAndModuleOrderNumber(
                                    request.getCourseId(),
                                    request.getOrderNumber()
                            )
                            .flatMap(exists -> {
                                if (!exists)
                                    return moduleFacade.createModule(request);
                                else {
                                    return Mono.error(
                                            Status.ALREADY_EXISTS
                                                    .withDescription(String.format(
                                                                             "Модуль с [num = %d ] уже есть"
                                                                                     + " в курсе [ ID = %d ]",
                                                                             request.getOrderNumber(),
                                                                             request.getCourseId())
                                                    ).asRuntimeException());
                                }
                            });
                } else {
                    return Mono.error(
                            Status.PERMISSION_DENIED
                                    .withDescription(String.format(
                                            "Юзер с [ ID = %d ] не имеет доступа к модулю с"
                                                    + "[ num = %d ] курса [ ID = %d ] ",
                                            request.getSenderId(),
                                            request.getOrderNumber(),
                                            request.getCourseId()
                                    )).asRuntimeException()
                    );
                }
            });
    }

    /**
     * Удаляет модуль из курса, если пользователь является автором курса
     *
     * @param request - gRPC-запрос, содержащий id курса и порядковый номер модуля
     * @return Mono с пустым gRPC-ответом
     */
    @Override
    public Mono<DeleteModuleResponse> deleteModule(DeleteModuleRequest request) {
        return accessChecker
                .isCourseAuthor(request.getSenderId(), request.getCourseId())
                .flatMap(isAuthor -> {
                    if (isAuthor) {
                        return moduleFacade
                                .findModuleResponseByCourseIdAndModuleOrderNum(
                                        request.getCourseId(), request.getModuleOrderNumber())
                                .map(ModuleResponse::getModuleId)
                                .flatMap(moduleFacade::deleteModuleById);
                    } else {
                        return Mono.error(
                                Status.PERMISSION_DENIED
                                        .withDescription(String.format(
                                                "Юзер с [ ID = %d ] не имеет доступа к модулю с"
                                                        + "[ num = %d ] курса [ ID = %d ] ",
                                                request.getSenderId(),
                                                request.getModuleOrderNumber(),
                                                request.getCourseId()
                                        )).asRuntimeException()
                        );
                    }
                });
    }

    /**
     * Импортирует модуль из файла в курс, если пользователь является автором курса и
     * модуля с таким порядковым номером ещё нет в курсе.
     *
     * @param request - gRPC - запрос с данными для импорта
     * @return - Mono с данными импортированного модуля
     */
    @Override
    @Transactional
    public Mono<ModuleResponse> importModuleFromFile(ImportModuleFromFileRequest request) {
        return accessChecker
                .isCourseAuthor(request.getSenderId(), request.getCourseId())
                .flatMap(isAuthor -> {
                    if (isAuthor) {
                        return moduleRepository.existsByCourseIdAndModuleOrderNumber(
                                                       request.getCourseId(), request.getOrderNumber())
                                               .flatMap(exists -> {
                                                   if (!exists) {
                                                       return moduleFacade.importModuleFromFile(
                                                               request);
                                                   } else {
                                                       return Mono.error(
                                                               Status.ALREADY_EXISTS
                                                                       .withDescription(String.format(
                                                                               "Модуль с [num = %d ] уже есть"
                                                                                       + " в курсе [ ID = %d ]",
                                                                               request.getOrderNumber(),
                                                                               request.getCourseId()
                                                                       )).asRuntimeException());
                                                   }
                                               });
                    } else {
                        return Mono.error(
                                Status.PERMISSION_DENIED
                                        .withDescription(String.format(
                                                "Юзер с [ ID = %d ] не имеет доступа к модулю с"
                                                        + "[ num = %d ] курса [ ID = %d ] ",
                                                request.getSenderId(),
                                                request.getOrderNumber(),
                                                request.getCourseId()
                                        )).asRuntimeException());
                    }
                });
    }
}