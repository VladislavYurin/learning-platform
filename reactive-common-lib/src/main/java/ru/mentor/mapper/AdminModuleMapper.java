package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import java.time.ZoneOffset;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.PageDetails;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;

/**
 * Converters between module entities and gRPC responses for admin flows.
 */
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

    public AllModulesResponse mapModuleResponsePageToGrpcAllModulesResponse(
            Page<ModuleResponse> modulesPage) {

        return AllModulesResponse.newBuilder()
                                 .setPageDetails(extractPageDetailsFromModuleResponsePage(
                                         modulesPage))
                                 .addAllModules(modulesPage)
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

}
