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
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.grpc.error.GrpcErrorText;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.facade.ModuleFacade;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class AdminModuleServiceServerTest {

    @Spy
    private BaseMapper baseMapper = new BaseMapper();
    @Mock
    private ModuleRepository moduleRepository;
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

        Mockito.when(moduleFacade.findModuleResponseById(TestConstantHolder.MODULE_ID))
                .thenReturn(Mono.just(expectedModuleResponse));

        StepVerifier.create(moduleServiceServer.getModule(Mono.just(moduleRequest)))
                .expectNext(expectedModuleResponse)
                .verifyComplete();
    }

    @Test
    void getModule_moduleNotFound_returnsNotFoundStatus() {
        GetModuleRequest getModuleRequest = TestGrpcStubGenerator.constructGetModuleRequest();

        Mockito.when(moduleFacade.findModuleResponseById(TestConstantHolder.MODULE_ID))
                .thenReturn(Mono.error(
                        new EntityNotFoundException(TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT)));

        StepVerifier.create(moduleServiceServer.getModule(Mono.just(getModuleRequest)))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(StatusRuntimeException.class, error);
                    StatusRuntimeException exception = (StatusRuntimeException) error;
                    Assertions.assertEquals(
                            Status.NOT_FOUND.getCode(),
                            exception.getStatus().getCode()
                    );
                    Assertions.assertEquals(
                            TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT,
                            exception.getStatus().getDescription()
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
                            Status.INVALID_ARGUMENT.getCode(),
                            statusRuntimeException.getStatus().getCode()
                    );
                    Assertions.assertEquals(
                            GrpcErrorText.EMPTY_REQUEST,
                            statusRuntimeException.getStatus().getDescription()
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
                            GrpcErrorText.EMPTY_REQUEST,
                            statusRuntimeException.getStatus().getDescription()
                    );
                })
                .verify();
    }

    @Test
    void getAllModules_successfulFlow_returnsResponse() {
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        AllModulesResponse expectedResponse = TestGrpcStubGenerator.constructAllModulesResponse();

        Mockito.when(moduleFacade.findAllModulesResponse(TestConstantHolder.PAGE_REQUEST))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(moduleServiceServer.getAllModules(Mono.just(grpcPageRequest)))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getAllModules_entitiesNotFound_returnsNotFoundStatus() {
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();

        Mockito.when(moduleFacade.findAllModulesResponse(TestConstantHolder.PAGE_REQUEST))
                .thenReturn(Mono.error(new EntityNotFoundException(TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT)));

        StepVerifier.create(moduleServiceServer.getAllModules(Mono.just(grpcPageRequest)))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(StatusRuntimeException.class, error);
                    StatusRuntimeException statusRuntimeException = (StatusRuntimeException) error;
                    Assertions.assertEquals(
                            Status.Code.NOT_FOUND,
                            statusRuntimeException.getStatus().getCode()
                    );
                    Assertions.assertEquals(
                            TestConstantHolder.NOT_FOUND_EXCEPTION_TEXT,
                            statusRuntimeException.getStatus().getDescription()
                    );
                })
                .verify();
    }
}