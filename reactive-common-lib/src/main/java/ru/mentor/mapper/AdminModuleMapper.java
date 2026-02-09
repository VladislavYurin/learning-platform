package ru.mentor.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.PageDetails;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;

/**
 * Converters between module entities and gRPC responses for admin flows.
 */
@Mapper(componentModel = "spring",
        uses = UtilMapper.class,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminModuleMapper {

    @Mapping(target = "moduleId", source = "id")
    @Mapping(target = "title", source = "moduleTitle")
    @Mapping(target = "orderNumber", source = "moduleOrderNumber")
    @Mapping(target = "content", source = "moduleContent")
    @Mapping(target = "createdAt",
            qualifiedByName = "localDateTimeToTimestamp")
    ModuleResponse mapModuleEntityToModuleResponse(ModuleEntity moduleEntity);

    default List<ModuleResponse> mapModuleEntityListToModuleResponseList(List<ModuleEntity> moduleEntityList) {
        return moduleEntityList.stream()
                .map(this::mapModuleEntityToModuleResponse)
                .toList();
    }

    default AllModulesResponse mapModuleResponsePageToAllModulesResponse(Page<ModuleResponse> moduleResponsesPage) {
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
    @Mapping(target = "moduleId", source = "moduleEntity.id")
    @Mapping(target = "title", source = "moduleEntity.moduleTitle")
    @Mapping(target = "orderNumber", source = "moduleEntity.moduleOrderNumber")
    @Mapping(target = "content", source = "moduleEntity.moduleContent")
    @Mapping(target = "isActive", source = "moduleEntity.isActive")
    @Mapping(target = "createdAt", source = "moduleEntity.createdAt",
            qualifiedByName = "localDateTimeToTimestamp")
    @Mapping(target = "courseId", source = "courseEntity.id")
    ModuleResponse mapModuleEntityToGrpcModuleResponse(
            CourseEntity courseEntity,
            ModuleEntity moduleEntity);

    @Mapping(target = "moduleTitle", source = "title")
    @Mapping(target = "moduleOrderNumber", source = "orderNumber")
    @Mapping(target = "moduleContent", source = "content")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt",
            expression = "java(utilMapper.getLocalDateTimeNow())")
    ModuleEntity createModuleGrpcRequestToModuleEntity(CreateModuleGrpcRequest request);

}