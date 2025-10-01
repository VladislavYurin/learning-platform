package ru.mentor.facade;

import reactor.core.publisher.Mono;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.ModuleResponse;

public interface ModuleFacade {

    Mono<ModuleResponse> findModuleResponseByCourseId(Long id);

    Mono<AllModulesResponse> findAllModulesAndMapToAllModulesResponse(Long id);

}
