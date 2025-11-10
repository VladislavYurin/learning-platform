package ru.mentor.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.ModuleResponse;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

class AdminModuleMapperTest {

    private final AdminModuleMapper mapper = new AdminModuleMapper();

    @Test
    void mapModuleEntityToModuleResponse_returnsExpectedResponse() {
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();
        moduleEntity.setId(TestConstantHolder.MODULE_ID);

        ModuleResponse response = mapper.mapModuleEntityToModuleResponse(moduleEntity);

        Assertions.assertEquals(TestConstantHolder.MODULE_ID, response.getModuleId());
        Assertions.assertEquals(TestConstantHolder.MODULE_TITLE, response.getTitle());
        Assertions.assertEquals(TestConstantHolder.MODULE_ORDER_NUMBER, response.getOrderNumber());
        Assertions.assertEquals(TestConstantHolder.MODULE_CONTENT, response.getContent());
        Assertions.assertEquals(TestConstantHolder.IS_ACTIVE_MODULE, response.getIsActive());
        Assertions.assertEquals(TestConstantHolder.COURSE_ID, response.getCourseId());
        Assertions.assertEquals(
                TestConstantHolder.MODULE_CREATED_AT_EPOCH_SECONDS,
                response.getCreatedAt().getSeconds()
        );
    }

    @Test
    void mapModuleResponsePageToAllModulesResponse_returnsExpectedAggregation() {
        Page<ModuleResponse> page = TestGrpcStubGenerator.constructModuleResponsePage();

        AllModulesResponse aggregated = mapper.mapModuleResponsePageToAllModulesResponse(page);
        ModuleResponse expectedModule = page.getContent().get(TestConstantHolder.PAGE_NUMBER);

        Assertions.assertEquals(page.getContent().size(), aggregated.getModulesCount());
        Assertions.assertEquals(
                expectedModule,
                aggregated.getModules(TestConstantHolder.PAGE_NUMBER)
        );
        Assertions.assertEquals(page.getNumber(), aggregated.getPageDetails().getPage());
        Assertions.assertEquals(page.getSize(), aggregated.getPageDetails().getSize());
        Assertions.assertEquals(
                page.getTotalElements(),
                aggregated.getPageDetails().getTotalElements()
        );
        Assertions.assertEquals(page.getTotalPages(), aggregated.getPageDetails().getTotalPages());
    }

    @Test
    void mapModuleEntityToGrpcModuleResponse_returnsResponseWithCourseData() {
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        courseEntity.setId(TestConstantHolder.COURSE_ID);
        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();
        moduleEntity.setId(TestConstantHolder.MODULE_ID);

        ModuleResponse response = mapper.mapModuleEntityToGrpcModuleResponse(
                courseEntity,
                moduleEntity
        );

        Assertions.assertEquals(TestConstantHolder.MODULE_ID, response.getModuleId());
        Assertions.assertEquals(TestConstantHolder.MODULE_TITLE, response.getTitle());
        Assertions.assertEquals(TestConstantHolder.MODULE_ORDER_NUMBER, response.getOrderNumber());
        Assertions.assertEquals(TestConstantHolder.MODULE_CONTENT, response.getContent());
        Assertions.assertEquals(TestConstantHolder.IS_ACTIVE_MODULE, response.getIsActive());
        Assertions.assertEquals(TestConstantHolder.COURSE_ID, response.getCourseId());
        Assertions.assertEquals(
                TestConstantHolder.MODULE_CREATED_AT_EPOCH_SECONDS,
                response.getCreatedAt().getSeconds()
        );
    }

}
