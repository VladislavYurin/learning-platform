package ru.mentor.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import ru.mentor.dto.PageSettings;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.GrpcRetryException;
import ru.mentor.grpc.AdminModuleServiceGrpcClient;
import ru.mentor.mapper.AdminModuleMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.services.impl.RedirectAdminModuleServiceImpl;

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

    private final String requestId = UUID.randomUUID().toString();
    private final int pageNumber = 0;
    private final int pageSize = 10;
    private final Long moduleId = 1L;
    private final String grpcExceptionText = "Ошибка gRPC";
    private final String moduleTitle = "Test Module";

    @Test
    void getModuleById_success() {
        GetModuleRequest grpcRequest = constructGetModuleRequest();
        ModuleResponse grpcResponse = constructModuleResponse();
        ModuleDto expectedDto = constructModuleDto();

        Mockito.when(moduleMapper.constructGetModuleRequest(any(), eq(moduleId)))
               .thenReturn(grpcRequest);
        Mockito.when(moduleGrpcClient.getModule(grpcRequest))
               .thenReturn(grpcResponse);
        Mockito.when(moduleMapper.mapGrpcModuleResponseToModuleDto(grpcResponse))
               .thenReturn(expectedDto);

        ModuleDto result = service.getModuleById(moduleId);

        assertThat(result).isEqualTo(expectedDto);
        Mockito.verify(moduleGrpcClient).getModule(grpcRequest);
    }

    @Test
    void getModuleById_failure() {
        GetModuleRequest grpcRequest = constructGetModuleRequest();

        Mockito.when(moduleMapper.constructGetModuleRequest(any(), eq(moduleId)))
               .thenReturn(grpcRequest);
        Mockito.when(moduleGrpcClient.getModule(grpcRequest))
               .thenThrow(new GrpcRetryException(grpcExceptionText, grpcRequest.getRequestId()));

        assertThatThrownBy(() -> service.getModuleById(moduleId))
                .isInstanceOf(GrpcRetryException.class)
                .hasMessageContaining(grpcExceptionText);

        Mockito.verify(moduleGrpcClient).getModule(grpcRequest);
    }

    @Test
    void getAllModules_success() {
        PageSettings pageSettings = constructPageSettings();
        GrpcPageRequest grpcPageRequest = constructGrpcPageRequest();
        AllModulesResponse grpcResponse = constructAllModulesResponse();
        Page<ModuleDto> expectedPage = constructExpectedPage();

        Mockito.when(baseMapper.constructGrpcPageRequest(any(), eq(pageSettings)))
               .thenReturn(grpcPageRequest);
        Mockito.when(moduleGrpcClient.getAllModules(grpcPageRequest))
               .thenReturn(grpcResponse);
        Mockito.when(moduleMapper.mapGrpcAllModulesResponseToModuleDtoPage(grpcResponse))
               .thenReturn(expectedPage);

        Page<ModuleDto> result = service.getAllModules(pageSettings);

        assertThat(result).isEqualTo(expectedPage);
        Mockito.verify(moduleGrpcClient).getAllModules(grpcPageRequest);
    }

    @Test
    void getAllModules_failure() {
        PageSettings pageSettings = constructPageSettings();
        GrpcPageRequest grpcPageRequest = constructGrpcPageRequest();

        Mockito.when(baseMapper.constructGrpcPageRequest(any(), eq(pageSettings)))
               .thenReturn(grpcPageRequest);
        Mockito.when(moduleGrpcClient.getAllModules(grpcPageRequest))
               .thenThrow(new GrpcRetryException(
                       grpcExceptionText,
                       grpcPageRequest.getRequestId()
               ));

        assertThatThrownBy(() -> service.getAllModules(pageSettings))
                .isInstanceOf(GrpcRetryException.class)
                .hasMessageContaining(grpcExceptionText);

        Mockito.verify(moduleGrpcClient).getAllModules(grpcPageRequest);
    }

    private Page<ModuleDto> constructExpectedPage() {
        return Page.empty();
    }

    private AllModulesResponse constructAllModulesResponse() {
        return AllModulesResponse.newBuilder().build();
    }

    private PageSettings constructPageSettings() {
        return new PageSettings(pageNumber, pageSize);
    }

    private ModuleDto constructModuleDto() {
        return ModuleDto.builder()
                        .id(moduleId)
                        .moduleTitle(moduleTitle)
                        .build();
    }

    private GrpcPageRequest constructGrpcPageRequest() {
        return GrpcPageRequest.newBuilder()
                              .setRequestId(requestId)
                              .setPageNumber(pageNumber)
                              .setPageSize(pageSize)
                              .build();
    }

    private UserEntity constructUserEntity() {
        return UserEntity.builder().build();
    }

    private ModuleResponse constructModuleResponse() {
        return ModuleResponse.newBuilder()
                             .setModuleId(moduleId)
                             .setTitle(moduleTitle)
                             .build();
    }

    private GetModuleRequest constructGetModuleRequest() {
        return GetModuleRequest.newBuilder()
                               .setRequestId(UUID.randomUUID().toString())
                               .setModuleId(moduleId)
                               .build();
    }

}