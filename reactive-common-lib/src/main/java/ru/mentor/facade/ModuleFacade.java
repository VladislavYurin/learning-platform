package ru.mentor.facade;

import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.DeleteModuleResponse;
import ru.mentor.common.ImportModuleFromFileRequest;
import ru.mentor.common.ModuleResponse;

public interface ModuleFacade {

    Mono<ModuleResponse> createModule(CreateModuleGrpcRequest request);

    Mono<ModuleResponse> findModuleResponseById(Long id);

    Mono<ModuleResponse> findModuleResponseByCourseIdAndModuleOrderNum(
            Long courseId,
            Integer moduleOrderNum);

    Mono<AllModulesResponse> findAllModulesResponse(PageRequest pageRequest);

    Mono<DeleteModuleResponse> deleteModuleById(Long moduleId);

    Mono<ModuleResponse> importModuleFromFile(ImportModuleFromFileRequest request);
}
