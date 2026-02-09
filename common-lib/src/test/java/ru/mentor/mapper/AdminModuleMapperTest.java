package ru.mentor.mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest(classes = {
        AdminModuleMapperImpl.class,
        BaseMapperImpl.class,
        UtilMapperImpl.class
})
class AdminModuleMapperTest {

    @Autowired
    private AdminModuleMapper adminModuleMapper;

    @Test
    void toGetModuleRequest_success() {
        Header header = TestGrpcStubGenerator.constructHeader();

        GetModuleRequest request = adminModuleMapper.toGetModuleRequest(
                header, TestConstantHolder.moduleId);

        Assertions.assertThat(request.getHeader().getRequestId())
                .isEqualTo(TestConstantHolder.requestId);
        Assertions.assertThat(request.getModuleId()).isEqualTo(TestConstantHolder.moduleId);
    }

    @Test
    void moduleResponseToModuleDto_success() {
        ModuleResponse grpcResponse = TestGrpcStubGenerator.constructModuleResponse();

        ModuleDto dto = adminModuleMapper.moduleResponseToModuleDto(grpcResponse);

        Assertions.assertThat(dto.getId()).isEqualTo(TestConstantHolder.moduleId);
        Assertions.assertThat(dto.getModuleTitle()).isEqualTo(TestConstantHolder.moduleTitle);
        Assertions.assertThat(dto.getModuleOrderNumber())
                .isEqualTo(TestConstantHolder.moduleOrderNumber);
        Assertions.assertThat(dto.getModuleContent()).isEqualTo(TestConstantHolder.moduleContent);
        Assertions.assertThat(dto.getIsActive()).isFalse();
        Assertions.assertThat(dto.getCreatedAt()).isEqualTo(TestConstantHolder.createdAt);
    }

    @Test
    void allModulesResponseToModuleDtoPage_success() {
        PageDetails pageDetails = TestGrpcStubGenerator.constructPageDetails();
        AllModulesResponse grpcResponse = TestGrpcStubGenerator.constructAllModulesResponse();

        Page<ModuleDto> result = adminModuleMapper.allModulesResponseToModuleDtoPage(
                grpcResponse);

        Assertions.assertThat(result.getContent()).hasSize(TestConstantHolder.totalPagesCount);
        Assertions.assertThat(result.getContent()
                        .get(TestConstantHolder.zero)
                        .getModuleTitle())
                .isEqualTo(TestConstantHolder.moduleTitle);
        Assertions.assertThat(result.getTotalElements())
                .isEqualTo(TestConstantHolder.totalElementsCount);
    }

    @Test
    void moduleEntityToModuleResponse_success() {
        ModuleEntity entity = TestEntityStubGenerator.constructModuleEntity();

        ModuleResponse response = adminModuleMapper.moduleEntityToModuleResponse(entity);

        Assertions.assertThat(response.getModuleId()).isEqualTo(TestConstantHolder.moduleId);
        Assertions.assertThat(response.getTitle()).isEqualTo(TestConstantHolder.moduleTitle);
        Assertions.assertThat(response.getOrderNumber())
                .isEqualTo(TestConstantHolder.moduleOrderNumber);
        Assertions.assertThat(response.getContent()).isEqualTo(TestConstantHolder.moduleContent);
        Assertions.assertThat(response.getIsActive()).isFalse();
        Assertions.assertThat(response.getCourseId()).isEqualTo(TestConstantHolder.courseId);
    }

    @Test
    void moduleEntityPageToAllModulesResponse_success() {
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();

        Page<ModuleEntity> page = TestEntityStubGenerator.constructModuleEntityPage(moduleEntity);

        AllModulesResponse response =
                adminModuleMapper.moduleEntityPageToAllModulesResponse(page);

        Assertions.assertThat(response.getModulesCount())
                .isEqualTo(TestConstantHolder.totalElementsCount);
        Assertions.assertThat(response.getModules(0).getTitle())
                .isEqualTo(TestConstantHolder.moduleTitle);
        Assertions.assertThat(response.getPageDetails().getTotalElements())
                .isEqualTo(TestConstantHolder.totalElementsCount);
    }

}
