package ru.mentor.mapper;

import java.util.Comparator;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.PageRequest;
import ru.mentor.common.GetAllModulesRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.common.PageDetails;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.entity.CourseTagLinkEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;

@Mapper(componentModel = "spring",
        uses = UtilMapper.class,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BaseMapper {

    default List<CourseDto> mapCourses(
            List<CourseEntity> entities,
            Boolean isNeedToFetchModules,
            Boolean isNeedToFetchSubmodules,
            Boolean isNeedToFetchTags) {
        return entities.stream()
                       .map(entity -> mapCourse(
                               entity,
                               entity.getAuthor(),
                               isNeedToFetchModules,
                               isNeedToFetchSubmodules,
                               isNeedToFetchTags
                       ))
                       .toList();
    }

    default CourseDto mapCourse(
            CourseEntity entity,
            UserEntity user,
            Boolean isNeedToFetchModules,
            Boolean isNeedToFetchModuleContent,
            Boolean isNeedToFetchTags) {
        return CourseDto.builder()
                        .id(entity.getId())
                        .courseTitle(entity.getCourseTitle())
                        .courseDescription(entity.getDescription())
                        .createdAt(entity.getCreatedAt())
                        .isActive(entity.getIsActive())
                        .author(user != null ? mapUserDto(user) : null)
                        .modules(
                                isNeedToFetchModules ? mapModules(
                                        entity.getModules(),
                                        isNeedToFetchModuleContent
                                ) : null)
                        .createdAt(entity.getCreatedAt())
                        .tags(isNeedToFetchTags ? mapTags(entity.getCourseTags()) : null)
                        .build();
    }

    default List<ModuleDto> mapModules(
            List<ModuleEntity> entities,
            Boolean isNeedToFetchModuleContent) {
        return entities.stream()
                       .map(module -> mapModule(module, isNeedToFetchModuleContent))
                       .sorted(Comparator.comparingInt(ModuleDto::getModuleOrderNumber))
                       .toList();
    }

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "moduleTitle", source = "entity.moduleTitle")
    @Mapping(target = "moduleOrderNumber", source = "entity.moduleOrderNumber")
    @Mapping(target = "moduleContent",
            expression = "java(Boolean.TRUE.equals(isNeedToFetchModuleContent) ? entity.getModuleContent() : null)")
    @Mapping(target = "isActive", source = "entity.isActive")
    @Mapping(target = "createdAt", source = "entity.createdAt")
    ModuleDto mapModule(ModuleEntity entity, Boolean isNeedToFetchModuleContent);

    @Named("mapUserDto")
    UserInfoDto mapUserDto(UserEntity entity);

    /**
     * Преобразует DTO информации о пользователе в сущность пользователя.
     *
     * @param userInfoDto
     *         DTO информации о пользователе для преобразования
     *
     * @return сущность пользователя
     */
    @Named("mapUserEntity")
    UserEntity mapUserEntity(UserInfoDto userInfoDto);

    /**
     * Создать gRPC-объект для запроса страницы любых объектов (аналог {@link PageRequest})
     *
     * @param header
     *         заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param pageNumber
     *         номер страницы
     * @param pageSize
     *         размер страницы
     *
     * @return gRPC-объект {@link GrpcPageRequest}
     */
    @Mapping(target = "senderId", ignore = true)
    GrpcPageRequest constructGrpcPageRequest(
            Header header,
            int pageNumber,
            int pageSize);

    /**
     * Создать gRPC-объект для запроса страницы любых объектов (аналог {@link PageRequest})
     *
     * @param header
     *         заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param pageNumber
     *         номер страницы
     * @param pageSize
     *         размер страницы
     * @param senderId
     *         id отправителя запроса
     *
     * @return gRPC-объект {@link GrpcPageRequest}
     */
    GrpcPageRequest constructGrpcPageRequest(
            Header header,
            int pageNumber,
            int pageSize,
            long senderId);

    /**
     * Преобразовать gRPC-объект в {@link PageRequest}
     *
     * @param pageDetails
     *         rRPC-объект {@link PageDetails}
     *
     * @return {@link PageRequest}
     */
    default PageRequest mapGrpcPageDetailsToPageRequest(PageDetails pageDetails) {
        return PageRequest.of(pageDetails.getPage(), pageDetails.getSize());
    }

    /**
     * Преобразовать gRPC-объект в {@link PageRequest}
     *
     * @param grpcPageRequest
     *         gRPC-объект {@link GrpcPageRequest}
     *
     * @return {@link PageRequest}
     */
    default PageRequest mapGrpcPageRequestToPageRequest(GrpcPageRequest grpcPageRequest) {
        return PageRequest.of(grpcPageRequest.getPageNumber(), grpcPageRequest.getPageSize());
    }

    @Mapping(target = "senderId", ignore = true)
    GetAllModulesRequest constructGetAllModulesRequest(Header header, long courseId);

    private List<CourseTagDto> mapTags(List<CourseTagLinkEntity> courseTags) {
        return courseTags.stream()
                         .map(ct -> {
                             CourseTagEntity tag = ct.getTag();
                             return CourseTagDto.builder()
                                                .id(tag.getId())
                                                .tagName(tag.getTagName())
                                                .isActive(tag.getIsActive())
                                                .createdAt(tag.getCreatedAt())
                                                .build();
                         })
                         .toList();
    }
}