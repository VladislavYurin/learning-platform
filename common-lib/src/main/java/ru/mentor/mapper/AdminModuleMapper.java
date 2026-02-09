package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.mentor.common.AllModulesResponse;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.Header;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.PageDetails;
import ru.mentor.dto.ModuleDto;
import ru.mentor.entity.ModuleEntity;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {BaseMapper.class,
                UtilMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AdminModuleMapper {

    @Autowired
    protected BaseMapper baseMapper;

    /**
     * Создает gRPC-объект.
     *
     * @param header   заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param moduleId ID модуля
     * @return gRPC-объект {@link GetModuleRequest}
     */
    public GetModuleRequest toGetModuleRequest(Header header, long moduleId) {
        return GetModuleRequest.newBuilder()
                .setHeader(header)
                .setModuleId(moduleId)
                .build();
    }

    /**
     * Преобразует gRPC-объект запроса модуля в DTO.
     *
     * @param grpcModuleResponse gRPC-объект запрошенного модуля
     * @return {@link ModuleDto}
     */
    @Mapping(target = "id", source = "moduleId")
    @Mapping(target = "moduleTitle", source = "title")
    @Mapping(target = "moduleOrderNumber", source = "orderNumber")
    @Mapping(target = "moduleContent", source = "content")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "createdAt", source = "createdAt",
            qualifiedByName = "timestampToLocalDateTime")
    public abstract ModuleDto moduleResponseToModuleDto(ModuleResponse grpcModuleResponse);

    /**
     * Преобразовать gRPC-объект списка модулей в страницу DTO.
     *
     * @param allModules gRPC-объект, содержащий список модулей
     * @return объект {@link Page}, содержащий DTO {@link ModuleDto}
     */
    public Page<ModuleDto> allModulesResponseToModuleDtoPage(AllModulesResponse allModules) {
        List<ModuleDto> moduleResponses = allModules.getModulesList().stream()
                .map(this::moduleResponseToModuleDto)
                .toList();
        PageDetails grpcPageDetails = allModules.getPageDetails();
        return new PageImpl<>(
                moduleResponses, baseMapper.pageDetailsToPageRequest(grpcPageDetails),
                grpcPageDetails.getTotalElements()
        );
    }

    /**
     * Преобразует сущностью модуля в gRPC-объект.
     *
     * @param moduleEntity сущность модуля
     * @return gRPC-объект {@link ModuleResponse}
     */
    @Mapping(target = "moduleId", source = "id")
    @Mapping(target = "title", source = "moduleTitle")
    @Mapping(target = "orderNumber", source = "moduleOrderNumber")
    @Mapping(target = "content", source = "moduleContent")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "createdAt", source = "createdAt",
            qualifiedByName = "localDateTimeToTimestamp")
    @Mapping(target = "courseId",
            expression = "java(moduleEntity.getCourse() != null ? " +
                    "moduleEntity.getCourse().getId() : 0)")
    public abstract ModuleResponse moduleEntityToModuleResponse(ModuleEntity moduleEntity);

    /**
     * Преобразует страницу сущностей модулей в gRPC-объект со списком модулей.
     *
     * @param modulesPage {@link Page} со списком {@link ModuleEntity}
     * @return {@link AllModulesResponse}
     */
    public AllModulesResponse moduleEntityPageToAllModulesResponse
    (Page<ModuleEntity> modulesPage) {

        List<ModuleResponse> moduleResponses = modulesPage.getContent().stream()
                .map(this::moduleEntityToModuleResponse)
                .toList();
        return AllModulesResponse.newBuilder()
                .setPageDetails(moduleEntityPageToPageDetails(modulesPage))
                .addAllModules(moduleResponses)
                .build();
    }

    private PageDetails moduleEntityPageToPageDetails(Page<ModuleEntity> modulesPage) {
        return PageDetails.newBuilder()
                .setPage(modulesPage.getNumber())
                .setSize(modulesPage.getSize())
                .setTotalElements(modulesPage.getTotalElements())
                .setTotalPages(modulesPage.getTotalPages())
                .build();
    }

}
