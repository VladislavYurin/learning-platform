package ru.mentor.grpc;

import static org.mockito.ArgumentMatchers.argThat;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

@ExtendWith(MockitoExtension.class)
class ModuleServiceServerTest {

    @Mock
    private ModuleRepository moduleRepository;
    @Mock
    private AdminModuleMapper moduleMapper;
    @Mock
    private BaseMapper baseMapper;

    @Mock
    private StreamObserver<ModuleResponse> moduleResponseObserver;
    @Mock
    private StreamObserver<AllModulesResponse> allModulesResponseObserver;

    @InjectMocks
    private ModuleServiceServer moduleServiceServer;

    private final String requestId = UUID.randomUUID().toString();
    private final int pageNumber = 0;
    private final int pageSize = 10;

    private final String notFoundExceptionText = "not found";

    private final long moduleId = 1L;

    private ModuleEntity constructModuleEntity() {
        return ModuleEntity.builder()
                           .id(moduleId)
                           .build();
    }

    private ModuleResponse constructModuleResponse() {
        return ModuleResponse.newBuilder()
                             .setModuleId(moduleId)
                             .build();
    }

    private GetModuleRequest constructModuleRequest() {
        return GetModuleRequest.newBuilder()
                               .setRequestId(requestId)
                               .setModuleId(moduleId)
                               .build();
    }

    private GrpcPageRequest constructGrpcPageRequest() {
        return GrpcPageRequest.newBuilder()
                              .setRequestId(requestId)
                              .build();
    }

    private AllModulesResponse constructAllModulesResponse() {
        return AllModulesResponse.newBuilder().build();
    }

    @Test
    void getModule_success() {
        ModuleEntity moduleEntity = constructModuleEntity();
        ModuleResponse moduleResponse = constructModuleResponse();
        GetModuleRequest request = constructModuleRequest();

        Mockito.when(moduleRepository.findByIdOrThrow(moduleId))
               .thenReturn(moduleEntity);
        Mockito.when(moduleMapper.mapModuleEntityToModuleResponse(
                moduleEntity)).thenReturn(moduleResponse);

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
        GetModuleRequest request = constructModuleRequest();

        Mockito.when(moduleRepository.findByIdOrThrow(moduleId))
               .thenThrow(new EntityNotFoundException(notFoundExceptionText));

        moduleServiceServer.getModule(request, moduleResponseObserver);

        Mockito.verify(moduleResponseObserver)
               .onError(argThat(throwable ->
                                        throwable instanceof StatusRuntimeException
                                                &&
                                                ((StatusRuntimeException) throwable).getStatus()
                                                                                    .getDescription()
                                                                                    .contains(
                                                                                            notFoundExceptionText)
               ));
        Mockito.verify(moduleResponseObserver, Mockito.never()).onNext(Mockito.any());
        Mockito.verify(moduleResponseObserver, Mockito.never()).onCompleted();
    }

    @Test
    void getAllModules_success() {
        GrpcPageRequest grpcRequest = constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<ModuleEntity> page = new PageImpl<>(
                List.of(new ModuleEntity()));

        AllModulesResponse grpcResponse = constructAllModulesResponse();

        Mockito.when(baseMapper.mapGrpcPageRequestToPageRequest(grpcRequest))
               .thenReturn(pageRequest);
        Mockito.when(moduleRepository.findAll(pageRequest)).thenReturn(page);
        Mockito.when(moduleMapper.mapModuleEntityPageToGrpcAllModulesResponse(page))
               .thenReturn(grpcResponse);

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
        GrpcPageRequest grpcRequest = constructGrpcPageRequest();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Mockito.when(baseMapper.mapGrpcPageRequestToPageRequest(grpcRequest))
               .thenReturn(pageRequest);
        Mockito.when(moduleRepository.findAll(pageRequest))
               .thenThrow(new EntityNotFoundException(notFoundExceptionText));

        moduleServiceServer.getAllModules(grpcRequest, allModulesResponseObserver);

        Mockito.verify(allModulesResponseObserver)
               .onError(argThat(throwable ->
                                        throwable instanceof StatusRuntimeException
                                                &&
                                                ((StatusRuntimeException) throwable).getStatus()
                                                                                    .getDescription()
                                                                                    .contains(
                                                                                            notFoundExceptionText)
               ));
        Mockito.verify(allModulesResponseObserver, Mockito.never()).onNext(Mockito.any());
        Mockito.verify(allModulesResponseObserver, Mockito.never()).onCompleted();
    }

}