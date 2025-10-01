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
import ru.mentor.entity.CourseEntity;
import ru.mentor.facade.ModuleFacade;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleFacadeImpl implements ModuleFacade {

    private final CourseRepository courseRepository;

    private final ModuleRepository moduleRepository;

    private final AdminModuleMapper moduleMapper;

    /**
     * Получает модуль по ID курса
     *
     * @param id
     *         - ID курса
     *
     * @return - gRPC-объект {@link ModuleResponse} модуля
     */
    @Override
    public Mono<ModuleResponse> findModuleResponseByCourseId(Long id) {
        return Mono.just(id)
                .flatMap(moduleId ->
                        moduleRepository.findByIdOrThrow(moduleId)
                                .flatMap(module ->
                                        courseRepository.findByIdOrThrow(module.getCourseId())
                                                .map(course -> moduleMapper.mapModuleEntityToGrpcModuleResponse(course, module))
                                )
                );
    }

    @Override
    public Mono<AllModulesResponse> findAllModulesAndMapToAllModulesResponse(Long id) {
        return Mono.just(id)
                   .flatMap(courseId -> {
                                Mono<CourseEntity> courseEntityMono =
                                        courseRepository.findByIdOrThrow(courseId)
                                                        .cache();
                                Mono<List<ModuleResponse>> moduleResponseItemsMono =
                                        getModuleResponseItemsMono(courseId, courseEntityMono);
                                Mono<Long> totalMono = moduleRepository.countByCourseId(courseId);
                                return Mono.zip(moduleResponseItemsMono, totalMono)
                                           .map(tuple -> {
                                                    List<ModuleResponse> moduleResponseList = tuple.getT1();
                                                    long total = tuple.getT2();
                                                    PageRequest pageable = PageRequest.of(
                                                            0, Math.max(1, moduleResponseList.size()));
                                                    PageImpl<ModuleResponse> page =
                                                            new PageImpl<>(
                                                                    moduleResponseList,
                                                                    pageable,
                                                                    total
                                                            );
                                                    return moduleMapper.mapModuleResponsePageToGrpcAllModulesResponse(
                                                            page);
                                                }
                                           );
                            }
                   );

    }

    private Mono<List<ModuleResponse>> getModuleResponseItemsMono(
            long courseId,
            Mono<CourseEntity> courseEntityMono) {
        return moduleRepository
                .findAllByCourseIdOrderByModuleOrderNumberAsc(courseId)
                .flatMap(module -> courseEntityMono.map(
                                 course -> moduleMapper.mapModuleEntityToGrpcModuleResponse(
                                         course,
                                         module
                                 )
                         )
                )
                .collectList();
    }

}