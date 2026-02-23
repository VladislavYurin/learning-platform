package ru.mentor.service;

import reactor.core.publisher.Mono;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.DeleteModuleRequest;
import ru.mentor.common.DeleteModuleResponse;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.ImportModuleFromFileRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.UpdateModuleGrpcRequest;

/**
 * Сервис для работы с модулями.
 * Интерфейс содержит методы для создания, удаления, получения и импорта модулей.
 */
public interface ModuleService {

    Mono<ModuleResponse> createModule(CreateModuleGrpcRequest request);

    Mono<ModuleResponse> updateModule(UpdateModuleGrpcRequest request);

    Mono<DeleteModuleResponse> deleteModule(DeleteModuleRequest request);

    Mono<ModuleResponse> getModule(GetModuleRequest request);

    Mono<ModuleResponse> importModuleFromFile(ImportModuleFromFileRequest request);
}
