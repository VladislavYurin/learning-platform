package ru.mentor.facade.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class ModuleFacadeImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Spy
    private AdminModuleMapper moduleMapper;

    @InjectMocks
    private ModuleFacadeImpl moduleFacade;

    @Test
    public void findAllModulesAndMapToAllModuleResponseByCourseId_success_returnsAllModulesResponse() {
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        AllModulesResponse allModulesResponse = TestGrpcStubGenerator.constructAllModulesResponse();

        Mockito.when(courseRepository.findByIdOrThrow(TestConstantHolder.COURSE_ID))
                       .thenReturn(Mono.just(courseEntity));
        Mockito.when(moduleRepository.countByCourseId(TestConstantHolder.COURSE_ID))
                       .thenReturn(Mono.just(TestConstantHolder.TOTAL_ELEMENTS_COUNT));
        Mockito.when(moduleRepository.findAllByCourseIdOrderByModuleOrderNumberAsc(TestConstantHolder.COURSE_ID))
                       .thenReturn(Flux.just(moduleEntity));

        StepVerifier.create(moduleFacade.findAllModulesAndMapToAllModulesResponse(TestConstantHolder.COURSE_ID))
                    .expectNext(allModulesResponse)
                    .verifyComplete();
    }

}