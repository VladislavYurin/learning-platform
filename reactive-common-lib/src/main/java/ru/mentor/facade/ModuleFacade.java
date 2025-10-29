package ru.mentor.facade;

import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.ModuleResponse;

public interface ModuleFacade {

    Mono<ModuleResponse> findModuleResponseById(Long id);

    Mono<AllModulesResponse> findAllModulesResponse(PageRequest pageRequest);

}
