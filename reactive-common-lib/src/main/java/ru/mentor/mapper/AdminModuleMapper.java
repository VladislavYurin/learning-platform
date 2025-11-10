package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.PageDetails;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;

/**
 * Converters between module entities and gRPC responses for admin flows.
 */
@Slf4j
@Component
public class AdminModuleMapper {

    public ModuleResponse mapModuleEntityToModuleResponse(ModuleEntity moduleEntity) {
        Timestamp createdAtTimestamp = Timestamp.newBuilder()
                                                .setSeconds(moduleEntity.getCreatedAt()
                                                                        .toEpochSecond(ZoneOffset.UTC))
                                                .build();
        return ModuleResponse.newBuilder()
                             .setModuleId(moduleEntity.getId())
                             .setTitle(moduleEntity.getModuleTitle())
                             .setOrderNumber(moduleEntity.getModuleOrderNumber())
                             .setContent(moduleEntity.getModuleContent())
                             .setIsActive(moduleEntity.getIsActive())
                             .setCreatedAt(createdAtTimestamp)
                             .setCourseId(moduleEntity.getCourseId())
                             .build();
    }

    public List<ModuleResponse> mapModuleEntityListToModuleResponseList(List<ModuleEntity> moduleEntityList) {
        return moduleEntityList.stream()
                               .map(this::mapModuleEntityToModuleResponse)
                               .toList();
    }

    public AllModulesResponse mapModuleResponsePageToAllModulesResponse(Page<ModuleResponse> moduleResponsesPage) {
        return AllModulesResponse.newBuilder()
                                 .setPageDetails(extractPageDetailsFromModuleResponsePage(
                                         moduleResponsesPage))
                                 .addAllModules(moduleResponsesPage)
                                 .build();
    }

    private PageDetails extractPageDetailsFromModuleResponsePage(Page<ModuleResponse> modulesPage) {
        return PageDetails.newBuilder()
                          .setPage(modulesPage.getNumber())
                          .setSize(modulesPage.getSize())
                          .setTotalElements(modulesPage.getTotalElements())
                          .setTotalPages(modulesPage.getTotalPages())
                          .build();
    }

    // TODO: Дублирует логику метода из этого класса, но неправильно
    public ModuleResponse mapModuleEntityToGrpcModuleResponse(
            CourseEntity courseEntity,
            ModuleEntity moduleEntity) {
        Timestamp createdAtTimestamp = Timestamp.newBuilder()
                                                .setSeconds(moduleEntity.getCreatedAt()
                                                                        .toEpochSecond(ZoneOffset.UTC))
                                                .build();
        return ModuleResponse.newBuilder()
                             .setModuleId(moduleEntity.getId())
                             .setTitle(moduleEntity.getModuleTitle())
                             .setOrderNumber(moduleEntity.getModuleOrderNumber())
                             .setContent(moduleEntity.getModuleContent())
                             .setIsActive(moduleEntity.getIsActive())
                             .setCreatedAt(createdAtTimestamp)
                             .setCourseId(courseEntity.getId())
                             .build();
    }

    public ModuleEntity mapCreateModuleGrpcRequestToModuleEntity(CreateModuleGrpcRequest request) {
        return ModuleEntity.builder()
                           .moduleTitle(request.getTitle())
                           .moduleContent(request.getContent())
                           .moduleOrderNumber(request.getOrderNumber())
                           .courseId(request.getCourseId())
                           .isActive(true)
                           .createdAt(LocalDateTime.now())
                           .build();
    }

}
