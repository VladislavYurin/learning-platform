package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.admin.GetModuleRequest;
import ru.mentor.admin.ModuleResponse;
import ru.mentor.admin.GetAllModulesRequest;
import ru.mentor.entity.CourseEntity;
import ru.mentor.admin.AllModulesResponse;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class AdminModuleServiceServerTest {

    @Spy
    private BaseMapper baseMapper = new BaseMapper();
    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private CourseRepository courseRepository;
    @Spy
    private AdminModuleMapper moduleMapper = new AdminModuleMapper(baseMapper);
    @InjectMocks
    private AdminModuleServiceServer moduleServiceServer;

    @Test
    void getModule_successfulFlow_returnsResponse() {
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();
        ModuleResponse expectedModuleResponse = TestGrpcStubGenerator.constructModuleResponse();
        GetModuleRequest moduleRequest = TestGrpcStubGenerator.constructGetModuleRequest();

        Mockito.when(moduleRepository.findByIdOrThrow(TestConstantHolder.MODULE_ID))
                .thenReturn(Mono.just(moduleEntity));

        StepVerifier.create(moduleServiceServer.getModule(moduleRequest))
                .expectNext(expectedModuleResponse)
                .verifyComplete();
    }

    @Test
    void getModule_moduleNotFound_returnsNotFoundStatus() {
        GetModuleRequest getModuleRequest = TestGrpcStubGenerator.constructGetModuleRequest();

        Mockito.when(moduleRepository.findByIdOrThrow(TestConstantHolder.MODULE_ID))
                .thenReturn(Mono.error(
                        new EntityNotFoundException(TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT)));

        StepVerifier.create(moduleServiceServer.getModule(Mono.just(getModuleRequest)))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(StatusRuntimeException.class, error);

                    StatusRuntimeException exception = (StatusRuntimeException) error;

                    Assertions.assertEquals(
                            exception.getStatus().getCode(),
                            Status.NOT_FOUND.getCode()
                    );
                    Assertions.assertEquals(
                            exception.getStatus().getDescription(),
                            TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT
                    );
                })
                .verify();
    }

    @Test
    void getModule_emptyRequest_returnsInvalidArgumentStatus() {
        Mono<GetModuleRequest> getModuleRequestMono = Mono.empty();

        StepVerifier.create(moduleServiceServer.getModule(getModuleRequestMono))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(StatusRuntimeException.class, error);

                    StatusRuntimeException statusRuntimeException = (StatusRuntimeException) error;

                    Assertions.assertEquals(
                            statusRuntimeException.getStatus().getCode(),
                            Status.INVALID_ARGUMENT.getCode()
                    );
                    Assertions.assertEquals(
                            statusRuntimeException.getStatus().getDescription(),
                            GrpcErrorText.EMPTY_REQUEST
                    );
                })
                .verify();
    }

    @Test
    void getAllModules_emptyRequest_returnsInvalidArgument(){
        StepVerifier.create(moduleServiceServer.getAllModules(Mono.empty()))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(StatusRuntimeException.class, error);

                    StatusRuntimeException statusRuntimeException = (StatusRuntimeException) error;
                    Assertions.assertEquals(
                            Status.Code.INVALID_ARGUMENT,
                            statusRuntimeException.getStatus().getCode()
                    );

                    Assertions.assertEquals(
                            TestConstantHolder.EMPTY_REQUEST_TEXT,
                            statusRuntimeException.getStatus().getDescription()
                    );
                })
                .verify();
    }

    @Test
    void getAllModules_successfulFlow_returnsResponse(){
        GetAllModulesRequest getAllModulesRequest = TestGrpcStubGenerator.constructGetAllModulesRequest();

        CourseEntity courseEntityStub = TestEntityStubGenerator.constructCourseEntity();
        ModuleEntity moduleEntityStub = TestEntityStubGenerator.constructModuleEntity();

        ModuleResponse expectedModuleResponse = TestGrpcStubGenerator.constructModuleResponse();
        AllModulesResponse getAllModulesResponse = TestGrpcStubGenerator.constructAllModulesResponse();

        Mockito.when(courseRepository.findByIdOrThrow(courseEntityStub.getId()))
                .thenReturn(Mono.just(courseEntityStub));
        Mockito.when(moduleRepository.findAllByCourseIdOrderByModuleOrderNumberAsc(moduleEntityStub.getCourseId()))
                .thenReturn(Flux.just(moduleEntityStub));
        Mockito.when(moduleRepository.findAllByCourseIdOrderByModuleOrderNumberAsc(TestConstantHolder.COURSE_ID))
                .thenReturn(Flux.just(moduleEntityStub));
        Mockito.when(moduleRepository.countByCourseId(moduleEntityStub.getCourseId()))
                .thenReturn(Mono.just(TestConstantHolder.TOTAL_ELEMENTS_COUNT));

        Mockito.when(moduleMapper.mapModuleEntityToGrpcModuleResponse(courseEntityStub, moduleEntityStub))
                .thenReturn(expectedModuleResponse);

        StepVerifier.create(moduleServiceServer.getAllModules(Mono.just(getAllModulesRequest)))
                .expectNext(getAllModulesResponse)
                .verifyComplete();

        Mockito.verify(courseRepository).findByIdOrThrow(moduleEntityStub.getCourseId());

        Mockito.verify(moduleRepository).findAllByCourseIdOrderByModuleOrderNumberAsc(moduleEntityStub.getCourseId());
        Mockito.verify(moduleRepository).countByCourseId(moduleEntityStub.getCourseId());

        Mockito.verify(moduleMapper).mapModuleEntityToGrpcModuleResponse(courseEntityStub, moduleEntityStub);

        Mockito.verify(moduleMapper, Mockito.times(1))
                .mapModuleEntityToGrpcModuleResponse(Mockito.any(CourseEntity.class), Mockito.any(ModuleEntity.class));
        Mockito.verify(moduleMapper, Mockito.times(1))
                .mapModuleResponsePageToGrpcAllModulesResponse(Mockito.any(Page.class));

        Mockito.verifyNoMoreInteractions(courseRepository, moduleRepository, moduleMapper);
}

    @Test
    void getAllModules_entitiesNotFound_returnsNotFoundStatus() {
        GetAllModulesRequest getAllModulesRequest = TestGrpcStubGenerator.constructGetAllModulesRequest();
        long expectedCount = 0L;

        Mockito.when(moduleRepository.findAllByCourseIdOrderByModuleOrderNumberAsc(getAllModulesRequest.getCourseId()))
                .thenReturn(Flux.error(new EntityNotFoundException(TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT)));

        Mockito.when(moduleRepository.countByCourseId(getAllModulesRequest.getCourseId()))
                .thenReturn(Mono.just(expectedCount));

        Mockito.when(courseRepository.findByIdOrThrow(getAllModulesRequest.getCourseId())).thenReturn(Mono.empty());

        StepVerifier.create(moduleServiceServer.getAllModules(getAllModulesRequest))
                .expectErrorSatisfies(error -> {

                    Assertions.assertInstanceOf(StatusRuntimeException.class, error);
                    StatusRuntimeException statusRuntimeException = (StatusRuntimeException) error;
                    Assertions.assertEquals(statusRuntimeException, error);

                    Assertions.assertEquals(
                            Status.Code.NOT_FOUND,
                            statusRuntimeException.getStatus().getCode()
                    );
                    Assertions.assertEquals(
                            statusRuntimeException.getStatus().getDescription(),
                            TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT
                    );
                })
                .verify();
    }
}