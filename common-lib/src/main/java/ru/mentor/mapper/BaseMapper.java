package ru.mentor.mapper;

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
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.CourseTagLinkEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.qualifier.NeedModuleContent;
import ru.mentor.mapper.qualifier.NeedModules;
import ru.mentor.mapper.qualifier.NeedTags;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = UtilMapper.class,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BaseMapper {

    /**
     * Преобразует список сущностей курсов в список DTO.
     * <p>
     * Для каждого курса автоматически используется
     * {@link #toCourseDto(CourseEntity, UserEntity, Boolean, Boolean, Boolean)}
     *
     * @param entities                   список сущностей курсов для преобразования
     * @param isNeedToFetchModules       флаг загрузки модулей для всех курсов
     * @param isNeedToFetchModuleContent флаг загрузки контента модулей для всех курсов
     * @param isNeedToFetchTags          флаг загрузки тегов для всех курсов
     * @return список DTO курсов
     */
    default List<CourseDto> courseEntityListToCourseDtoList(
            List<CourseEntity> entities,
            Boolean isNeedToFetchModules,
            Boolean isNeedToFetchModuleContent,
            Boolean isNeedToFetchTags) {
        return entities.stream()
                .map(entity -> toCourseDto(
                        entity,
                        entity.getAuthor(),
                        isNeedToFetchModules,
                        isNeedToFetchModuleContent,
                        isNeedToFetchTags
                ))
                .toList();
    }

    /**
     * Преобразует сущности курса и пользователя в DTO информацию о курсе
     * <p>
     * Метод поддерживает условный маппинг связанных сущностей для оптимизации запросов
     *
     * @param entity                     сущность курса для преобразования
     * @param user                       сущность пользователя для преобразования
     * @param isNeedToFetchModules       флаг, указывающий нужно ли загружать модули курса
     *                                   Если {@code true},
     *                                   вызывается {@link #moduleEntityListToModuleDtoList(List, Boolean)}
     * @param isNeedToFetchModuleContent флаг, указывающий нужно ли загружать контент модулей
     *                                   Используется только если {@code isNeedToFetchModules == true}
     * @param isNeedToFetchTags          флаг, указывающий нужно ли загружать теги курса
     *                                   Если {@code true},
     *                                   вызывается {@link #courseTagLinkEntityListToCourseTagDtoList(List)}
     * @return DTO информация о курсе
     */
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "courseTitle", source = "entity.courseTitle")
    @Mapping(target = "courseDescription", source = "entity.description")
    @Mapping(target = "isActive", source = "entity.isActive")
    @Mapping(target = "createdAt", source = "entity.createdAt")
    @Mapping(target = "author", source = "user")
    @Mapping(target = "modules",
            expression = "java(entity != null && Boolean.TRUE.equals(isNeedToFetchModules)" +
                    " ? moduleEntityListToModuleDtoList(entity.getModules(), isNeedToFetchModuleContent)" +
                    " : null)")
    @Mapping(target = "tags",
            expression = "java(entity != null && Boolean.TRUE.equals(isNeedToFetchTags)" +
                    " ? courseTagLinkEntityListToCourseTagDtoList(entity.getCourseTags())" +
                    " : null)")
    CourseDto toCourseDto(CourseEntity entity,
                          UserEntity user,
                          @NeedModules Boolean isNeedToFetchModules,
                          @NeedModuleContent Boolean isNeedToFetchModuleContent,
                          @NeedTags Boolean isNeedToFetchTags);

    /**
     * Преобразует список сущностей модулей в список DTO
     * <p>
     * Для каждого модуля автоматически используется {@link #moduleEntityToModuleDto(ModuleEntity, Boolean)}
     *
     * @param modules                    список сущностей модулей
     * @param isNeedToFetchModuleContent флаг загрузки контента модулей
     * @return список DTO модулей
     */
    @Named("moduleEntityListToModuleDtoList")
    default List<ModuleDto> moduleEntityListToModuleDtoList(
            List<ModuleEntity> modules,
            Boolean isNeedToFetchModuleContent) {
        return modules.stream()
                .map(module -> moduleEntityToModuleDto(module, isNeedToFetchModuleContent))
                .sorted(Comparator.comparingInt(ModuleDto::getModuleOrderNumber))
                .toList();
    }

    /**
     * Преобразует сущность модуля в DTO модуля
     *
     * @param entity                     сущность модуля для преобразования
     * @param isNeedToFetchModuleContent если {@code true}, загружается {@code moduleContent},
     *                                   иначе поле устанавливается в {@code null}
     * @return DTO модуля
     */
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "moduleTitle", source = "entity.moduleTitle")
    @Mapping(target = "moduleOrderNumber", source = "entity.moduleOrderNumber")
    @Mapping(target = "moduleContent",
            expression = "java(Boolean.TRUE.equals(isNeedToFetchModuleContent) ? entity.getModuleContent() : null)")
    @Mapping(target = "isActive", source = "entity.isActive")
    @Mapping(target = "createdAt", source = "entity.createdAt")
    ModuleDto moduleEntityToModuleDto(ModuleEntity entity,
                                      Boolean isNeedToFetchModuleContent);

    /**
     * Преобразует список связей курс-тег в список DTO тегов.
     *
     * @param courseTags список связей {@code CourseTagLinkEntity}
     * @return список DTO тегов
     */
    @Named("courseTagLinkEntityListToCourseTagDtoList")
    default List<CourseTagDto> courseTagLinkEntityListToCourseTagDtoList(List<CourseTagLinkEntity> courseTags) {
        return courseTags.stream()
                .map(courseTag -> courseTagLinkEntityToCourseTagDto(courseTag))
                .toList();
    }

    /**
     * Преобразует связь курс-тег в DTO тега.
     * <p>
     * Извлекает информацию из вложенного объекта {@code Tag}.
     *
     * @param courseTag связь курс-тег для преобразования
     * @return DTO тега
     */
    @Mapping(target = "id", source = "tag.id")
    @Mapping(target = "tagName", source = "tag.tagName")
    @Mapping(target = "createdAt", source = "tag.createdAt")
    @Mapping(target = "isActive", source = "tag.isActive")
    CourseTagDto courseTagLinkEntityToCourseTagDto(CourseTagLinkEntity courseTag);

    /**
     * Создать gRPC-объект для запроса страницы любых объектов (аналог {@link PageRequest})
     *
     * @param header     заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param pageNumber номер страницы
     * @param pageSize   размер страницы
     * @return gRPC-объект {@link GrpcPageRequest}
     */
    default GrpcPageRequest toGrpcPageRequest(Header header,
                                              int pageNumber,
                                              int pageSize) {
        return GrpcPageRequest.newBuilder()
                .setHeader(header)
                .setPageNumber(pageNumber)
                .setPageSize(pageSize)
                .build();
    }

    /**
     * Создать gRPC-объект для запроса страницы любых объектов (аналог {@link PageRequest})
     *
     * @param header     заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param pageNumber номер страницы
     * @param pageSize   размер страницы
     * @param senderId   id отправителя запроса
     * @return gRPC-объект {@link GrpcPageRequest}
     */
    default GrpcPageRequest toGrpcPageRequest(Header header,
                                              int pageNumber,
                                              int pageSize,
                                              long senderId) {
        return GrpcPageRequest.newBuilder()
                .setHeader(header)
                .setPageNumber(pageNumber)
                .setPageSize(pageSize)
                .setSenderId(senderId)
                .build();
    }

    /**
     * Преобразовать gRPC-объект в {@link PageRequest}
     *
     * @param pageDetails rRPC-объект {@link PageDetails}
     * @return {@link PageRequest}
     */
    default PageRequest pageDetailsToPageRequest(PageDetails pageDetails) {
        return PageRequest.of(pageDetails.getPage(), pageDetails.getSize());
    }

    /**
     * Преобразовать gRPC-объект в {@link PageRequest}
     *
     * @param grpcPageRequest gRPC-объект {@link GrpcPageRequest}
     * @return {@link PageRequest}
     */
    default PageRequest grpcPageRequestToPageRequest(GrpcPageRequest grpcPageRequest) {
        return PageRequest.of(grpcPageRequest.getPageNumber(), grpcPageRequest.getPageSize());
    }

    default GetAllModulesRequest toGetAllModulesRequest(Header header,
                                                        long courseId) {
        return GetAllModulesRequest.newBuilder()
                .setHeader(header)
                .setCourseId(courseId)
                .build();
    }
}