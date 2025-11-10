package ru.mentor.facade.impl;

import io.grpc.Status;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.DeleteModuleResponse;
import ru.mentor.common.ImportModuleFromFileRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.facade.ModuleFacade;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.validation.MarkdownConverter;
import ru.mentor.validation.MarkdownValidator;

/**
 * Фасад для работы с модулями.
 * Абстракция для работы со связанными таблицами в реактивных репозиториях и для маппинга.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleFacadeImpl implements ModuleFacade {

    private final ModuleRepository moduleRepository;

    private final AdminModuleMapper moduleMapper;

    private final MarkdownValidator markdownValidator;

    /**
     * Находит модуль по ID
     *
     * @param moduleId
     *         - ID модуля в таблице modules
     *
     * @return - Mono с DTO {@link ModuleResponse} модуля
     */
    @Override
    public Mono<ModuleResponse> findModuleResponseById(Long moduleId) {
        return moduleRepository.findByIdOrThrow(moduleId)
                               .map(moduleMapper::mapModuleEntityToModuleResponse);
    }

    /**
     * Находит модуль по ID курса и порядковому номеру модуля
     *
     * @param courseId - ID курса в таблице courses
     * @param moduleOrderNum - порядковый номер модуля в курсе
     *
     * @return - DTO с данными модуля
     */
    @Override
    public Mono<ModuleResponse> findModuleResponseByCourseIdAndModuleOrderNum(
            Long courseId,
            Integer moduleOrderNum) {

        return moduleRepository.findByCourseIdAndModuleOrderNumberOrThrow(courseId, moduleOrderNum)
                               .map(moduleMapper::mapModuleEntityToModuleResponse)
                               .onErrorMap(EntityNotFoundException.class, e -> Status.NOT_FOUND
                                       .withDescription(e.getMessage())
                                       .asRuntimeException());
    }

    /**
     * Создает новый модуль в курсе
     *
     * @param request - DTO с данными нового модуля
     *
     * @return - Mono с DTO созданного модуля
     */
    @Override
    public Mono<ModuleResponse> createModule(CreateModuleGrpcRequest request) {
        ModuleEntity newModule = moduleMapper.mapCreateModuleGrpcRequestToModuleEntity(request);

        return moduleRepository.save(newModule)
                               .map(moduleMapper::mapModuleEntityToModuleResponse);
    }

    /**
     * Находит модули и возвращает страницу с пагинацией
     *
     * @param pageRequest
     *         - параметры пагинации
     *
     * @return - Mono с DTO {@link AllModulesResponse} со списком модулей и информацией о пагинации
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
     * Удаляет модуль по его ID
     *
     * @param moduleId - ID модуля в таблице modules
     *
     * @return - DTO с пустым ответом
     */
    @Override
    public Mono<DeleteModuleResponse> deleteModuleById(Long moduleId) {
        return moduleRepository
                .deleteById(moduleId)
                .thenReturn(DeleteModuleResponse.newBuilder().build());
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

    /**
     * Импортирует модуль из файла
     *
     * @param request - DTO запроса на импорт
     *
     * @return Mono с DTO импортированного модуля
     */
    @Override
    public Mono<ModuleResponse> importModuleFromFile(ImportModuleFromFileRequest request) {
        String contentType = determineContentType(request.getFilename());

        return markdownValidator.validate(request.getFileContent().toByteArray(),
                                          request.getFilename(),
                                          contentType)
                                .then(Mono.fromCallable(() -> {
                                    String markdownContent = new String(
                                        request.getFileContent().toByteArray(),
                                        StandardCharsets.UTF_8
                                    );

                                    String htmlContent =
                                        MarkdownConverter.markdownToHtml(markdownContent);

                                    return ModuleEntity.builder()
                                                       .moduleTitle(request.getTitle())
                                                       .moduleContent(htmlContent)
                                                       .courseId(request.getCourseId())
                                                       .createdAt(LocalDateTime.now())
                                                       .moduleOrderNumber(request.getOrderNumber())
                                                       .isActive(true)
                                                       .build();
                                }))
                                .flatMap(moduleRepository::save)
                                .map(moduleMapper::mapModuleEntityToModuleResponse);
    }

    /**
     * Определяет тип файла
     *
     * @param filename - имя файла
     *
     * @return строка с типом файла
     */
    private String determineContentType(String filename) {
        if (filename.toLowerCase().endsWith(".md"))
            return "text/markdown";

        return "application/octet-stream";
    }

    /**
     * Возвращает общее количество модулей в репозитории
     * @return количетво модулей
     */
    private Mono<Long> moduleTotalCount() {
        return moduleRepository.count();
    }

}