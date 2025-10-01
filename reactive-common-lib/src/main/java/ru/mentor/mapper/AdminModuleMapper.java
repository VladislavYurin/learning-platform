package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import ru.mentor.admin.AllModulesResponse;
import ru.mentor.admin.GetModuleRequest;
import ru.mentor.admin.ModuleResponse;
import ru.mentor.admin.PageDetails;
import ru.mentor.dto.ModuleDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;

@Component
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class AdminModuleMapper {

    private final BaseMapper baseMapper;

    /**
     * Создает gRPC-объект.
     *
     * @param requestId
     *         сквозной UUID запроса
     * @param moduleId
     *         ID модуля
     *
     * @return gRPC-объект {@link GetModuleRequest}
     */
    public GetModuleRequest constructGetModuleRequest(String requestId, long moduleId) {
        return GetModuleRequest.newBuilder()
                               .setRequestId(requestId)
                               .setModuleId(moduleId)
                               .build();
    }

    /**
     * Преобразует gRPC-объект запроса модуля в DTO.
     *
     * @param grpcModuleResponse
     *         gRPC-объект запрошенного модуля
     *
     * @return {@link ModuleDto}
     */
    public ModuleDto mapGrpcModuleResponseToModuleDto(ModuleResponse grpcModuleResponse) {
        LocalDateTime createdAtDateTime = LocalDateTime.ofEpochSecond(
                grpcModuleResponse.getCreatedAt().getSeconds(),
                grpcModuleResponse.getCreatedAt().getNanos(),
                ZoneOffset.UTC
        );
        return ModuleDto.builder()
                        .id(grpcModuleResponse.getModuleId())
                        .moduleTitle(grpcModuleResponse.getTitle())
                        .moduleOrderNumber(grpcModuleResponse.getOrderNumber())
                        .moduleContent(grpcModuleResponse.getContent())
                        .isActive(grpcModuleResponse.getIsActive())
                        .createdAt(createdAtDateTime)
                        .build();
    }

    /**
     * Преобразовать gRPC-объект списка модулей в страницу DTO.
     *
     * @param allModules
     *         gRPC-объект, содержащий список модулей
     *
     * @return объект {@link Page}, содержащий DTO {@link ModuleDto}
     */
    public Page<ModuleDto> mapGrpcAllModulesResponseToModuleDtoPage(AllModulesResponse allModules) {
        List<ModuleDto> moduleResponses = allModules.getModulesList().stream()
                                                    .map(this::mapGrpcModuleResponseToModuleDto)
                                                    .toList();
        PageDetails grpcPageDetails = allModules.getPageDetails();
        return new PageImpl<>(
                moduleResponses, baseMapper.mapGrpcPageDetailsToPageRequest(grpcPageDetails),
                grpcPageDetails.getTotalElements()
        );
    }

    /**
     * Преобразует сущностью модуля в gRPC-объект.
     *
     * @param moduleEntity
     *         сущность модуля
     *
     * @return gRPC-объект {@link ModuleResponse}
     */
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

    /**
     * Преобразует страницу сущностей модулей в gRPC-объект со списком модулей.
     *
     * @param modulesPage
     *         {@link Page} со списком {@link ModuleEntity}
     *
     * @return {@link AllModulesResponse}
     */
    public AllModulesResponse mapModuleEntityPageToGrpcAllModulesResponse
    (Page<ModuleEntity> modulesPage) {

        List<ModuleResponse> moduleResponses = modulesPage.getContent().stream()
                                                          .map(this::mapModuleEntityToModuleResponse)
                                                          .toList();
        return AllModulesResponse.newBuilder()
                                 .setPageDetails(extractPageDetailsFromModuleEntityPage(modulesPage))
                                 .addAllModules(moduleResponses)
                                 .build();
    }

    private PageDetails extractPageDetailsFromModuleEntityPage(Page<ModuleEntity> modulesPage) {
        return PageDetails.newBuilder()
                          .setPage(modulesPage.getNumber())
                          .setSize(modulesPage.getSize())
                          .setTotalElements(modulesPage.getTotalElements())
                          .setTotalPages(modulesPage.getTotalPages())
                          .build();
    }

    /**
     * Преобразует страницу модулей в gRPC-объект со списком модулей.
     *
     * @param modulesPage
     *              страница с элементами модулей и метаданными пагинации
     *
     * @return собранный gRPC-ответ с заполненными PageDetails и списком модулей
     */
    public AllModulesResponse mapModuleResponsePageToGrpcAllModulesResponse
            (Page<ModuleResponse> modulesPage) {
        return AllModulesResponse.newBuilder()
                                 .setPageDetails(extractPageDetailsFromModuleResponsePage(modulesPage))
                                 .addAllModules(modulesPage)
                                 .build();
    }

    /**
     * Формирует gRPC-объект из метаданных страницы
     *
     * @param modulesPage
     *              страница с номером, размером и общим количеством элементов
     *
     * @return заполненный объект для включения в gRPC-ответ
     */
    private PageDetails extractPageDetailsFromModuleResponsePage(Page<ModuleResponse> modulesPage) {
        return PageDetails.newBuilder()
                          .setPage(modulesPage.getNumber())
                          .setSize(modulesPage.getSize())
                          .setTotalElements(modulesPage.getTotalElements())
                          .setTotalPages(modulesPage.getTotalPages())
                          .build();
    }

    /**
     * Преобразует сущность курса и модуля в gRPC-объект
     *
     * @param courseEntity
     *              сущность курса
     *
     * @param moduleEntity
     *              сущность модуля
     *
     * @return gRPC-представление модуля с id курса для ответа
     */
    public ModuleResponse mapModuleEntityToGrpcModuleResponse(CourseEntity courseEntity, ModuleEntity moduleEntity) {
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
