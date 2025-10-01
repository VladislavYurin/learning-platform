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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.GetAllModulesRequest;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.facade.ModuleFacade;
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
    private AdminModuleMapper moduleMapper = new AdminModuleMapper();
    @Mock
    private ModuleFacade moduleFacade;
    @InjectMocks
    private AdminModuleServiceServer moduleServiceServer;

    @Test
    void getModule_successfulFlow_returnsResponse() {
        ModuleResponse expectedModuleResponse = TestGrpcStubGenerator.constructModuleResponse();
        GetModuleRequest moduleRequest = TestGrpcStubGenerator.constructGetModuleRequest();

        Mockito.when(moduleFacade.findModuleResponseByCourseId(TestConstantHolder.MODULE_ID))
                .thenReturn(Mono.just(expectedModuleResponse));

        StepVerifier.create(moduleServiceServer.getModule(Mono.just(moduleRequest)))
                .expectNext(expectedModuleResponse)
                .verifyComplete();
    }

    @Test
    void getModule_moduleNotFound_returnsNotFoundStatus() {
        GetModuleRequest getModuleRequest = TestGrpcStubGenerator.constructGetModuleRequest();

        Mockito.when(moduleFacade.findModuleResponseByCourseId(TestConstantHolder.MODULE_ID))
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
    void getAllModules_emptyRequest_returnsInvalidArgument() {
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
    void getAllModules_successfulFlow_returnsResponse() {
        GetAllModulesRequest getAllModulesRequest = TestGrpcStubGenerator.constructGetAllModulesRequest();
        AllModulesResponse getAllModulesResponse = TestGrpcStubGenerator.constructAllModulesResponse();

        Mockito.when(moduleFacade.findAllModulesAndMapToAllModulesResponse(TestConstantHolder.COURSE_ID))
               .thenReturn(Mono.just(getAllModulesResponse));

        StepVerifier.create(moduleServiceServer.getAllModules(Mono.just(getAllModulesRequest)))
                    .expectNext(getAllModulesResponse)
                    .verifyComplete();
    }

    @Test
    void getAllModules_entitiesNotFound_returnsNotFoundStatus() {
        GetAllModulesRequest getAllModulesRequest = TestGrpcStubGenerator.constructGetAllModulesRequest();

        Mockito.when(moduleFacade.findAllModulesAndMapToAllModulesResponse(TestConstantHolder.COURSE_ID))
               .thenReturn(Mono.error(new EntityNotFoundException(TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT)));

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