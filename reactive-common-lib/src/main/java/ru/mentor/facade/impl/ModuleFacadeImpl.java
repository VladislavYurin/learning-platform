package ru.mentor.facade.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.ModuleResponse;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.facade.ModuleFacade;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;

/**
 * Фасад для работы с модулями.
 * Абстракция для работы со связанными таблицами в реактивных репозиториях и для маппинга.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleFacadeImpl implements ModuleFacade {

    private final CourseRepository courseRepository;

    private final ModuleRepository moduleRepository;

    private final AdminModuleMapper moduleMapper;

    /**
     * Получает модуль по ID
     *
     * @param moduleId
     *         - ID модуля
     *
     * @return - gRPC-объект {@link ModuleResponse} модуля
     */
    @Override
    public Mono<ModuleResponse> findModuleResponseById(Long moduleId) {
        return moduleRepository.findByIdOrThrow(moduleId)
                               .map(moduleMapper::mapModuleEntityToModuleResponse);
    }

    /**
     * Получает страницу с модулями
     *
     * @param pageRequest
     *         - параметры пагинации
     *
     * @return - gRPC-объект {@link AllModulesResponse} со списком модулей и информацией о пагинации
     */
    @Override
    public Mono<AllModulesResponse> findAllModulesResponse(PageRequest pageRequest) {
        return Mono.just(pageRequest)
                   .flatMap(this::findModuleEntitiesByPageRequest)
                   .map(moduleMapper::mapModuleEntityListToModuleResponseList)
                   .flatMap(moduleResponseList -> constructModuleResponsePage(
                                    pageRequest,
                                    moduleResponseList
                            )
                   ).map(moduleMapper::mapModuleResponsePageToAllModulesResponse);

    }

    /**
     * Находит список сущностей модулей по параметрам пагинации
     *
     * @param pageRequest
     *         - параметры пагинации
     *
     * @return - {@link Mono} со списком сущностей модулей
     */
    private Mono<List<ModuleEntity>> findModuleEntitiesByPageRequest(
            PageRequest pageRequest) {
        return moduleRepository
                .findAllBy(pageRequest)
                .collectList();
    }

    /**
     * Создает страницу с ответами по модулям
     *
     * @param pageRequest
     *         - параметры пагинации
     * @param moduleResponseList
     *         - список ответов по модулям
     *
     * @return - {@link Mono} с объектом {@link PageImpl} содержащим ответы по модулям
     */
    private Mono<PageImpl<ModuleResponse>> constructModuleResponsePage(
            PageRequest pageRequest,
            List<ModuleResponse> moduleResponseList) {
        return moduleTotalCount()
                .zipWith(
                        Mono.just(moduleResponseList),
                        (moduleTotalCount, moduleResponses) -> new PageImpl<>(
                                moduleResponses, pageRequest, moduleTotalCount)
                );
    }

    private Mono<Long> moduleTotalCount() {
        return moduleRepository.count();
    }

}