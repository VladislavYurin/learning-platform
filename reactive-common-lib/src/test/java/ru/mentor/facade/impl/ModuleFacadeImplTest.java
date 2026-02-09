package ru.mentor.facade.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.ModuleResponse;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ModuleFacadeImplTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private AdminModuleMapper moduleMapper;

    @InjectMocks
    private ModuleFacadeImpl moduleFacade;

    /**
     * Успешное получение модуля и связанного с ним курса
     */
    @Test
    void findModuleResponseById_whenModuleExist_shouldReturnModuleResponse() {
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();
        moduleEntity.setId(TestConstantHolder.MODULE_ID);
        ModuleResponse expectedResponse = TestGrpcStubGenerator.constructModuleResponse();

        Mockito.when(moduleRepository.findByIdOrThrow(TestConstantHolder.MODULE_ID))
                .thenReturn(Mono.just(moduleEntity));
        Mockito.when(moduleMapper.mapModuleEntityToModuleResponse(moduleEntity)).thenReturn(
                expectedResponse);

        Mono<ModuleResponse> result = moduleFacade.findModuleResponseById(TestConstantHolder.MODULE_ID);

        StepVerifier.create(result).expectNext(expectedResponse).verifyComplete();

        Mockito.verify(moduleRepository).findByIdOrThrow(TestConstantHolder.MODULE_ID);
        Mockito.verify(moduleMapper).mapModuleEntityToModuleResponse(moduleEntity);
    }

    /**
     * Обработка ситуации, когда модуль не найден
     */
    @Test
    void findModuleResponseById_whenMappingFails_shouldThrowException() {
        String errorMessage = "Module not found";
        Mockito.when(moduleRepository.findByIdOrThrow(TestConstantHolder.MODULE_ID))
                .thenReturn(Mono.error(new EntityNotFoundException(errorMessage)));

        Mono<ModuleResponse> result = moduleFacade.findModuleResponseById(TestConstantHolder.MODULE_ID);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof EntityNotFoundException
                        && throwable.getMessage()
                        .equals(errorMessage))
                .verify();

        Mockito.verify(moduleRepository).findByIdOrThrow(TestConstantHolder.MODULE_ID);
    }

    /**
     * Обработка ситуации, когда курс не найден
     */
    @Test
    void findModuleResponseById_whenCourseNotFound_shouldThrowException() {
        String errorMessage = "Course not found";
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();
        moduleEntity.setId(TestConstantHolder.MODULE_ID);

        Mockito.when(moduleRepository.findByIdOrThrow(TestConstantHolder.MODULE_ID))
                .thenReturn(Mono.just(moduleEntity));
        Mockito.when(moduleMapper.mapModuleEntityToModuleResponse(moduleEntity))
                .thenThrow(new EntityNotFoundException(errorMessage));

        StepVerifier.create(moduleFacade.findModuleResponseById(TestConstantHolder.MODULE_ID))
                .expectErrorMatches(throwable -> throwable instanceof EntityNotFoundException
                        && throwable.getMessage().equals(errorMessage))
                .verify();
        Mockito.verify(moduleRepository).findByIdOrThrow(TestConstantHolder.MODULE_ID);
        Mockito.verify(moduleMapper).mapModuleEntityToModuleResponse(moduleEntity);
    }

    @Test
    public void findAllModulesResponse_success_returnsAllModulesResponse() {
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();
        moduleEntity.setId(TestConstantHolder.MODULE_ID);
        List<ModuleEntity> moduleEntityList = List.of(moduleEntity);
        ModuleResponse response = TestGrpcStubGenerator.constructModuleResponse();
        List<ModuleResponse> responseList = List.of(response);

                AllModulesResponse allModulesResponse = TestGrpcStubGenerator.constructAllModulesResponse();

        Mockito.when(moduleRepository.findAllBy(TestConstantHolder.PAGE_REQUEST))
                .thenReturn(Flux.just(moduleEntity));
        Mockito.when(moduleRepository.count())
                .thenReturn(Mono.just(TestConstantHolder.TOTAL_ELEMENTS_COUNT));
        Mockito.when(moduleMapper.mapModuleEntityListToModuleResponseList(moduleEntityList))
                .thenReturn(responseList);
        Mockito.when(moduleMapper.mapModuleResponsePageToAllModulesResponse(TestGrpcStubGenerator.constructModuleResponsePage()))
                .thenReturn(TestGrpcStubGenerator.constructAllModulesResponse());

        StepVerifier.create(moduleFacade.findAllModulesResponse(TestConstantHolder.PAGE_REQUEST))
                .expectNext(allModulesResponse)
                .verifyComplete();
    }

}