package ru.mentor.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.mentor.admin.AllModulesResponse;
import ru.mentor.admin.GetModuleRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.ModuleResponse;
import ru.mentor.dto.ModuleDto;
import ru.mentor.exception.GrpcRetryException;
import ru.mentor.grpc.AdminModuleServiceGrpcClient;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.services.impl.RedirectAdminModuleServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class RedirectAdminModuleServiceImplTest {

    @Mock
    private AdminModuleServiceGrpcClient moduleGrpcClient;

    @Mock
    private AdminModuleMapper moduleMapper;

    @Mock
    private UserService userService;

    @Mock
    private BaseMapper baseMapper;

    @InjectMocks
    private RedirectAdminModuleServiceImpl service;

    @Test
    void getModuleById_success() {
        GetModuleRequest grpcRequest = TestGrpcStubGenerator.constructGetModuleRequest();
        ModuleResponse grpcResponse = TestGrpcStubGenerator.constructModuleResponse();
        ModuleDto expectedDto = TestEntityStubGenerator.constructModuleDto();

        Mockito.when(moduleMapper.constructGetModuleRequest(
                       ArgumentMatchers.any(),
                       ArgumentMatchers.eq(TestConstantHolder.moduleId)
               ))
               .thenReturn(grpcRequest);
        Mockito.when(moduleGrpcClient.getModule(grpcRequest))
               .thenReturn(grpcResponse);
        Mockito.when(moduleMapper.mapGrpcModuleResponseToModuleDto(grpcResponse))
               .thenReturn(expectedDto);

        ModuleDto result = service.getModuleById(TestConstantHolder.moduleId);

        Assertions.assertThat(result).isEqualTo(expectedDto);
        Mockito.verify(moduleGrpcClient).getModule(grpcRequest);
    }

    @Test
    void getModuleById_failure() {
        GetModuleRequest grpcRequest = TestGrpcStubGenerator.constructGetModuleRequest();

        Mockito.when(moduleMapper.constructGetModuleRequest(
                       ArgumentMatchers.any(),
                       ArgumentMatchers.eq(TestConstantHolder.moduleId)
               ))
               .thenReturn(grpcRequest);
        Mockito.when(moduleGrpcClient.getModule(grpcRequest))
               .thenThrow(new GrpcRetryException(
                       TestConstantHolder.grpcExceptionText,
                       grpcRequest.getRequestId()
               ));

        Assertions.assertThatThrownBy(() -> service.getModuleById(TestConstantHolder.moduleId))
                  .isInstanceOf(GrpcRetryException.class)
                  .hasMessageContaining(TestConstantHolder.grpcExceptionText);

        Mockito.verify(moduleGrpcClient).getModule(grpcRequest);
    }

    @Test
    void getAllModules_success() {
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();
        AllModulesResponse grpcResponse = TestGrpcStubGenerator.constructAllModulesResponse();
        Page<ModuleDto> expectedPage = TestEntityStubGenerator.constructModuleDtoPage();

        Mockito.when(baseMapper.constructGrpcPageRequest(
                       ArgumentMatchers.any(),
                       ArgumentMatchers.anyInt(),
                       ArgumentMatchers.anyInt()
               ))
               .thenReturn(grpcPageRequest);
        Mockito.when(moduleGrpcClient.getAllModules(grpcPageRequest))
               .thenReturn(grpcResponse);
        Mockito.when(moduleMapper.mapGrpcAllModulesResponseToModuleDtoPage(grpcResponse))
               .thenReturn(expectedPage);

        Page<ModuleDto> result = service.getAllModules(
                TestConstantHolder.pageNumber,
                TestConstantHolder.pageSize
        );

        Assertions.assertThat(result).isEqualTo(expectedPage);
        Mockito.verify(moduleGrpcClient).getAllModules(grpcPageRequest);
    }

    @Test
    void getAllModules_failure() {
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();

        Mockito.when(baseMapper.constructGrpcPageRequest(
                       ArgumentMatchers.any(),
                       ArgumentMatchers.anyInt(),
                       ArgumentMatchers.anyInt()
               ))
               .thenReturn(grpcPageRequest);
        Mockito.when(moduleGrpcClient.getAllModules(grpcPageRequest))
               .thenThrow(new GrpcRetryException(
                       TestConstantHolder.grpcExceptionText,
                       grpcPageRequest.getRequestId()
               ));

        Assertions.assertThatThrownBy(() -> service.getAllModules(
                          TestConstantHolder.pageNumber,
                          TestConstantHolder.pageSize
                  ))
                  .isInstanceOf(GrpcRetryException.class)
                  .hasMessageContaining(TestConstantHolder.grpcExceptionText);

        Mockito.verify(moduleGrpcClient).getAllModules(grpcPageRequest);
    }

}