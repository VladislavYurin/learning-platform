package ru.mentor.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
import ru.mentor.admin.ModuleResponse;
import ru.mentor.admin.PageDetails;
import ru.mentor.dto.ModuleDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;

@ExtendWith(MockitoExtension.class)
class AdminModuleMapperTest {

    @Mock
    private BaseMapper baseMapper;

    @InjectMocks
    private AdminModuleMapper adminModuleMapper;

    private final String requestId = UUID.randomUUID().toString();
    private final long moduleId = 1L;
    private final LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC);
    private final int totalElementsCount = 1;
    private final int totalPagesCount = 1;
    private final long courseId = 1L;
    private final String moduleTitle = "Test Module";
    private final int moduleOrderNumber = 5;
    private final String moduleContent = "Content";
    private final boolean isActive = true;
    private final int pageNumber = 0;
    private final int pageSize = 10;

    @Test
    void constructGetModuleRequest_success() {
        GetModuleRequest request = adminModuleMapper.constructGetModuleRequest(requestId, moduleId);

        assertThat(request.getRequestId()).isEqualTo(requestId);
        assertThat(request.getModuleId()).isEqualTo(moduleId);
    }

    @Test
    void mapGrpcModuleResponseToModuleDto_success() {
        ModuleResponse grpcResponse = constructModuleResponse();

        ModuleDto dto = adminModuleMapper.mapGrpcModuleResponseToModuleDto(grpcResponse);

        assertThat(dto.getId()).isEqualTo(moduleId);
        assertThat(dto.getModuleTitle()).isEqualTo(moduleTitle);
        assertThat(dto.getModuleOrderNumber()).isEqualTo(moduleOrderNumber);
        assertThat(dto.getModuleContent()).isEqualTo(moduleContent);
        assertThat(dto.getIsActive()).isTrue();
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void mapGrpcAllModulesResponseToModuleDtoPage_success() {
        PageRequest pageRequest = constructPageRequest();
        ModuleResponse grpcModule = constructModuleResponse();

        Mockito.when(baseMapper.mapGrpcPageDetailsToPageRequest(Mockito.any()))
               .thenReturn(pageRequest);

        PageDetails pageDetails = constructPageDetails();

        AllModulesResponse grpcResponse = AllModulesResponse.newBuilder()
                                                            .addModules(grpcModule)
                                                            .setPageDetails(pageDetails)
                                                            .build();

        Page<ModuleDto> result = adminModuleMapper.mapGrpcAllModulesResponseToModuleDtoPage(
                grpcResponse);

        assertThat(result.getContent()).hasSize(totalPagesCount);
        assertThat(result.getContent().get(pageNumber).getModuleTitle()).isEqualTo(moduleTitle);
        assertThat(result.getTotalElements()).isEqualTo(totalElementsCount);
        Mockito.verify(baseMapper).mapGrpcPageDetailsToPageRequest(pageDetails);
    }

    @Test
    void mapModuleEntityToModuleResponse_success() {
        ModuleEntity entity = constructModuleEntity();

        ModuleResponse response = adminModuleMapper.mapModuleEntityToModuleResponse(entity);

        assertThat(response.getModuleId()).isEqualTo(moduleId);
        assertThat(response.getTitle()).isEqualTo(moduleTitle);
        assertThat(response.getOrderNumber()).isEqualTo(moduleOrderNumber);
        assertThat(response.getContent()).isEqualTo(moduleContent);
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getCourseId()).isEqualTo(courseId);
    }

    @Test
    void mapModuleEntityPageToGrpcAllModulesResponse_success() {
        ModuleEntity moduleEntity = constructModuleEntity();

        Page<ModuleEntity> page = constructModuleEntityPage(moduleEntity);

        AllModulesResponse response =
                adminModuleMapper.mapModuleEntityPageToGrpcAllModulesResponse(page);

        assertThat(response.getModulesCount()).isEqualTo(totalElementsCount);
        assertThat(response.getModules(0).getTitle()).isEqualTo(moduleTitle);
        assertThat(response.getPageDetails().getTotalElements()).isEqualTo(totalElementsCount);
    }

    private Page<ModuleEntity> constructModuleEntityPage(ModuleEntity moduleEntity) {
        return new PageImpl<>(
                List.of(moduleEntity),
                PageRequest.of(pageNumber, pageSize),
                totalElementsCount
        );
    }

    private ModuleEntity constructModuleEntity() {
        return ModuleEntity.builder()
                           .id(moduleId)
                           .moduleTitle(moduleTitle)
                           .moduleOrderNumber(moduleOrderNumber)
                           .moduleContent(moduleContent)
                           .isActive(isActive)
                           .createdAt(LocalDateTime.now())
                           .course(constructCourseEntity())
                           .build();
    }

    private CourseEntity constructCourseEntity() {
        return CourseEntity.builder()
                           .id(courseId)
                           .build();
    }

    private PageDetails constructPageDetails() {
        return PageDetails.newBuilder()
                          .setPage(pageNumber)
                          .setSize(pageSize)
                          .setTotalElements(totalElementsCount)
                          .setTotalPages(totalPagesCount)
                          .build();
    }

    private PageRequest constructPageRequest() {
        return PageRequest.of(pageNumber, pageSize);
    }

    private ModuleResponse constructModuleResponse() {
        return ModuleResponse.newBuilder()
                             .setModuleId(moduleId)
                             .setTitle(moduleTitle)
                             .setOrderNumber(moduleOrderNumber)
                             .setContent(moduleContent)
                             .setIsActive(isActive)
                             .setCreatedAt(constructCreatedAtTimestamp())
                             .build();
    }

    private Timestamp constructCreatedAtTimestamp() {
        return Timestamp.newBuilder()
                        .setSeconds(createdAt.toEpochSecond(ZoneOffset.UTC))
                        .setNanos(createdAt.getNano())
                        .build();
    }

}