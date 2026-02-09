package ru.mentor.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.dto.ModuleDto;
import ru.mentor.exception.GrpcRetryException;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.AdminModuleServiceGrpcClient;
import ru.mentor.mapper.AdminModuleMapperImpl;
import ru.mentor.mapper.BaseMapperImpl;
import ru.mentor.mapper.UtilMapperImpl;
import ru.mentor.services.impl.RedirectAdminModuleServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@SpringBootTest(classes = {
        RedirectAdminModuleServiceImpl.class,
        BaseMapperImpl.class,
        AdminModuleMapperImpl.class,
        UtilMapperImpl.class})
class RedirectAdminModuleServiceImplTest {

    @MockBean
    private AdminModuleServiceGrpcClient moduleGrpcClient;

    @MockBean
    private UserService userService;

    @MockBean
    private HeaderFactory headerFactory;

    @Autowired
    private RedirectAdminModuleServiceImpl service;

    @BeforeEach
    void setUp() {
        Mockito.when(headerFactory.create(ArgumentMatchers.anyString()))
               .thenReturn(TestGrpcStubGenerator.constructHeader());
    }

    @Test
    void getModuleById_success() {
        ModuleResponse grpcResponse = TestGrpcStubGenerator.constructModuleResponse();
        ModuleDto expectedDto = TestEntityStubGenerator.constructModuleDto();

        Mockito.when(userService.getCurrentUserId()).thenReturn(TestConstantHolder.userId);
        Mockito.when(moduleGrpcClient.getModule(ArgumentMatchers.any(GetModuleRequest.class)))
               .thenReturn(grpcResponse);

        ModuleDto result = service.getModuleById(TestConstantHolder.moduleId);

        Assertions.assertThat(result).isEqualTo(expectedDto);

        ArgumentCaptor<GetModuleRequest> captor = ArgumentCaptor.forClass(GetModuleRequest.class);
        Mockito.verify(moduleGrpcClient).getModule(captor.capture());

        GetModuleRequest actualRequest = captor.getValue();
        Assertions.assertThat(actualRequest.getModuleId()).isEqualTo(TestConstantHolder.moduleId);
        Assertions.assertThat(actualRequest.getHeader().getRequestId()).isNotBlank();
    }

    @Test
    void getModuleById_failure() {
        Mockito.when(userService.getCurrentUserId()).thenReturn(TestConstantHolder.userId);
        Mockito.when(moduleGrpcClient.getModule(ArgumentMatchers.any(GetModuleRequest.class)))
               .thenAnswer(invocation -> {
                   GetModuleRequest request = invocation.getArgument(0, GetModuleRequest.class);
                   throw new GrpcRetryException(
                           TestConstantHolder.grpcExceptionText,
                           request.getHeader().getRequestId()
                   );
               });

        Assertions.assertThatThrownBy(() -> service.getModuleById(TestConstantHolder.moduleId))
                  .isInstanceOf(GrpcRetryException.class)
                  .hasMessageContaining(TestConstantHolder.grpcExceptionText);

        ArgumentCaptor<GetModuleRequest> captor = ArgumentCaptor.forClass(GetModuleRequest.class);
        Mockito.verify(moduleGrpcClient).getModule(captor.capture());

        GetModuleRequest actualRequest = captor.getValue();
        Assertions.assertThat(actualRequest.getModuleId()).isEqualTo(TestConstantHolder.moduleId);
        Assertions.assertThat(actualRequest.getHeader().getRequestId()).isNotBlank();
    }

    @Test
    void getAllModules_success() {
        AllModulesResponse grpcResponse = TestGrpcStubGenerator.constructAllModulesResponse();
        Page<ModuleDto> expectedPage = TestEntityStubGenerator.constructModuleDtoPage();

        Mockito.when(userService.getCurrentUserId()).thenReturn(TestConstantHolder.userId);
        Mockito.when(moduleGrpcClient.getAllModules(ArgumentMatchers.any(GrpcPageRequest.class)))
               .thenReturn(grpcResponse);

        Page<ModuleDto> result = service.getAllModules(
                TestConstantHolder.zero,
                TestConstantHolder.pageSize
        );

        Assertions.assertThat(result.getContent()).isEqualTo(expectedPage.getContent());
        Assertions.assertThat(result.getTotalElements()).isEqualTo(expectedPage.getTotalElements());
        Assertions.assertThat(result.getPageable()).isEqualTo(expectedPage.getPageable());

        ArgumentCaptor<GrpcPageRequest> captor = ArgumentCaptor.forClass(GrpcPageRequest.class);
        Mockito.verify(moduleGrpcClient).getAllModules(captor.capture());

        GrpcPageRequest actualRequest = captor.getValue();
        Assertions.assertThat(actualRequest.getPageNumber())
                  .isEqualTo(TestConstantHolder.zero);
        Assertions.assertThat(actualRequest.getPageSize()).isEqualTo(TestConstantHolder.pageSize);
        Assertions.assertThat(actualRequest.getHeader().getRequestId()).isNotBlank();
    }

    @Test
    void getAllModules_failure() {
        GrpcPageRequest grpcPageRequest = TestGrpcStubGenerator.constructGrpcPageRequest();

        Mockito.when(userService.getCurrentUserId()).thenReturn(TestConstantHolder.userId);

        Mockito.when(moduleGrpcClient.getAllModules(ArgumentMatchers.any(GrpcPageRequest.class)))
               .thenAnswer(invocation -> {
                   GrpcPageRequest request = invocation.getArgument(0, GrpcPageRequest.class);
                   throw new GrpcRetryException(
                           TestConstantHolder.grpcExceptionText,
                           request.getHeader().getRequestId()
                   );
               });

        Assertions.assertThatThrownBy(() -> service.getAllModules(
                          TestConstantHolder.zero,
                          TestConstantHolder.pageSize
                  ))
                  .isInstanceOf(GrpcRetryException.class)
                  .hasMessageContaining(TestConstantHolder.grpcExceptionText);

        ArgumentCaptor<GrpcPageRequest> captor = ArgumentCaptor.forClass(GrpcPageRequest.class);
        Mockito.verify(moduleGrpcClient).getAllModules(captor.capture());

        GrpcPageRequest actualRequest = captor.getValue();
        Assertions.assertThat(actualRequest.getPageNumber())
                  .isEqualTo(grpcPageRequest.getPageNumber());
        Assertions.assertThat(actualRequest.getPageSize()).isEqualTo(grpcPageRequest.getPageSize());
        Assertions.assertThat(actualRequest.getHeader().getRequestId()).isNotBlank();
    }

}
