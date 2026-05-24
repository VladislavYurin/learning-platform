package ru.mentor.mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.Header;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.PageDetails;
import ru.mentor.dto.ModuleDto;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class AdminModuleMapperTest {

    @Spy
    private BaseMapper baseMapper = new BaseMapperImpl();

    @InjectMocks
    private AdminModuleMapperImpl adminModuleMapper;

    @Test
    void constructGetModuleRequest_success() {
        Header header = Header.newBuilder()
                              .setRequestId(TestConstantHolder.requestId)
                              .setNodeId(TestConstantHolder.nodeId)
                              .setApiKey(TestConstantHolder.apiKey)
                              .build();

        GetModuleRequest request = adminModuleMapper.constructGetModuleRequest(
                header, TestConstantHolder.moduleId);

        Assertions.assertThat(request.getHeader().getRequestId())
                  .isEqualTo(TestConstantHolder.requestId);
        Assertions.assertThat(request.getModuleId()).isEqualTo(TestConstantHolder.moduleId);
    }

    @Test
    void mapGrpcModuleResponseToModuleDto_success() {
        ModuleResponse grpcResponse = TestGrpcStubGenerator.constructModuleResponse();

        ModuleDto dto = adminModuleMapper.mapGrpcModuleResponseToModuleDto(grpcResponse);

        Assertions.assertThat(dto.getId()).isEqualTo(TestConstantHolder.moduleId);
        Assertions.assertThat(dto.getModuleTitle()).isEqualTo(TestConstantHolder.moduleTitle);
        Assertions.assertThat(dto.getModuleOrderNumber())
                  .isEqualTo(TestConstantHolder.moduleOrderNumber);
        Assertions.assertThat(dto.getModuleContent()).isEqualTo(TestConstantHolder.moduleContent);
        Assertions.assertThat(dto.getIsActive()).isTrue();
        Assertions.assertThat(dto.getCreatedAt()).isEqualTo(TestConstantHolder.createdAt);
    }

    @Test
    void mapGrpcAllModulesResponseToModuleDtoPage_success() {
        PageDetails pageDetails = TestGrpcStubGenerator.constructPageDetails();
        AllModulesResponse grpcResponse = TestGrpcStubGenerator.constructAllModulesResponse();

        Page<ModuleDto> result = adminModuleMapper.mapGrpcAllModulesResponseToModuleDtoPage(
                grpcResponse);

        Assertions.assertThat(result.getContent()).hasSize(TestConstantHolder.totalPagesCount);
        Assertions.assertThat(result.getContent()
                                    .get(TestConstantHolder.pageNumber)
                                    .getModuleTitle())
                  .isEqualTo(TestConstantHolder.moduleTitle);
        Assertions.assertThat(result.getTotalElements())
                  .isEqualTo(TestConstantHolder.totalElementsCount);
        Mockito.verify(baseMapper).mapGrpcPageDetailsToPageRequest(pageDetails);
    }

    @Test
    void mapModuleEntityToModuleResponse_success() {
        ModuleEntity entity = TestEntityStubGenerator.constructModuleEntity();

        ModuleResponse response = adminModuleMapper.mapModuleEntityToModuleResponse(entity);

        Assertions.assertThat(response.getModuleId()).isEqualTo(TestConstantHolder.moduleId);
        Assertions.assertThat(response.getTitle()).isEqualTo(TestConstantHolder.moduleTitle);
        Assertions.assertThat(response.getOrderNumber())
                  .isEqualTo(TestConstantHolder.moduleOrderNumber);
        Assertions.assertThat(response.getContent()).isEqualTo(TestConstantHolder.moduleContent);
        Assertions.assertThat(response.getIsActive()).isTrue();
        Assertions.assertThat(response.getCourseId()).isEqualTo(TestConstantHolder.courseId);
    }

    @Test
    void mapModuleEntityPageToGrpcAllModulesResponse_success() {
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();

        Page<ModuleEntity> page = TestEntityStubGenerator.constructModuleEntityPage(moduleEntity);

        AllModulesResponse response =
                adminModuleMapper.mapModuleEntityPageToGrpcAllModulesResponse(page);

        Assertions.assertThat(response.getModulesCount())
                  .isEqualTo(TestConstantHolder.totalElementsCount);
        Assertions.assertThat(response.getModules(0).getTitle())
                  .isEqualTo(TestConstantHolder.moduleTitle);
        Assertions.assertThat(response.getPageDetails().getTotalElements())
                  .isEqualTo(TestConstantHolder.totalElementsCount);
    }

}
