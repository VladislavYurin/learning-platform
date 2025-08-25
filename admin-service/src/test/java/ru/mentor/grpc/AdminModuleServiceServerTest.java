package ru.mentor.grpc;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mentor.admin.AllModulesResponse;
import ru.mentor.admin.GetModuleRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.ModuleResponse;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.mapper.BaseMapper;
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
    @Spy
    private AdminModuleMapper moduleMapper = new AdminModuleMapper(baseMapper);
    @Mock
    private StreamObserver<ModuleResponse> moduleResponseObserver;
    @Mock
    private StreamObserver<AllModulesResponse> allModulesResponseObserver;
    @Captor
    private ArgumentCaptor<Throwable> entityNotFoundCaptor;
    @InjectMocks
    private AdminModuleServiceServer moduleServiceServer;

    @Test
    void getModule_success() {
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();
        ModuleResponse moduleResponse = TestGrpcStubGenerator.constructModuleResponse();
        GetModuleRequest request = TestGrpcStubGenerator.constructGetModuleRequest();

        Mockito.when(moduleRepository.findByIdOrThrow(TestConstantHolder.moduleId))
               .thenReturn(moduleEntity);

        moduleServiceServer.getModule(request, moduleResponseObserver);

        Mockito.verify(moduleResponseObserver).onNext(moduleResponse);
        Mockito.verify(moduleResponseObserver).onCompleted();
        Mockito.verify(
                moduleResponseObserver,
                Mockito.never()
        ).onError(Mockito.any());
    }

    @Test
    void getModule_notFound() {
        GetModuleRequest request = TestGrpcStubGenerator.constructGetModuleRequest();

        Mockito.when(moduleRepository.findByIdOrThrow(TestConstantHolder.moduleId))
               .thenThrow(new EntityNotFoundException(TestConstantHolder.notFoundExceptionText));

        moduleServiceServer.getModule(request, moduleResponseObserver);

        Mockito.verify(moduleResponseObserver).onError(entityNotFoundCaptor.capture());
        Mockito.verify(moduleResponseObserver, Mockito.never()).onNext(Mockito.any());
        Mockito.verify(moduleResponseObserver, Mockito.never()).onCompleted();

        Throwable entityNotFoundException = entityNotFoundCaptor.getValue();
        Assertions.assertThat(entityNotFoundException)
                  .isInstanceOf(StatusRuntimeException.class)
                  .hasMessageContaining(TestConstantHolder.notFoundExceptionText);
    }

    @Test
    void getAllModules_success() {
        GrpcPageRequest grpcRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(
                TestConstantHolder.pageNumber,
                TestConstantHolder.pageSize
        );
        Page<ModuleEntity> page = new PageImpl<>(
                List.of(TestEntityStubGenerator.constructModuleEntity()));

        AllModulesResponse grpcResponse = TestGrpcStubGenerator.constructAllModulesResponse();

        Mockito.when(moduleRepository.findAll(pageRequest)).thenReturn(page);

        moduleServiceServer.getAllModules(grpcRequest, allModulesResponseObserver);

        Mockito.verify(allModulesResponseObserver).onNext(grpcResponse);
        Mockito.verify(allModulesResponseObserver).onCompleted();
        Mockito.verify(
                allModulesResponseObserver,
                Mockito.never()
        ).onError(Mockito.any());
    }

    @Test
    void getAllModules_notFound() {
        GrpcPageRequest grpcRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(
                TestConstantHolder.pageNumber,
                TestConstantHolder.pageSize
        );

        Mockito.when(moduleRepository.findAll(pageRequest))
               .thenThrow(new EntityNotFoundException(TestConstantHolder.notFoundExceptionText));

        moduleServiceServer.getAllModules(grpcRequest, allModulesResponseObserver);

        Mockito.verify(allModulesResponseObserver).onError(entityNotFoundCaptor.capture());
        Mockito.verify(allModulesResponseObserver, Mockito.never()).onNext(Mockito.any());
        Mockito.verify(allModulesResponseObserver, Mockito.never()).onCompleted();

        Throwable entityNotFoundException = entityNotFoundCaptor.getValue();
        Assertions.assertThat(entityNotFoundException)
                  .isInstanceOf(StatusRuntimeException.class)
                  .hasMessageContaining(TestConstantHolder.notFoundExceptionText);
    }

}