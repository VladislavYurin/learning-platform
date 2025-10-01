package ru.mentor.mapper;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.mentor.admin.GetAllModulesRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.PageDetails;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.entity.CourseTagLinkEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;

@Component
@RequiredArgsConstructor
public class BaseMapper {

    public List<CourseDto> mapCourses(
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

    public CourseDto mapCourse(
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

    public List<ModuleDto> mapModules(
            List<ModuleEntity> entities,
            Boolean isNeedToFetchModuleContent) {
        return entities.stream()
                       .map(module -> mapModule(module, isNeedToFetchModuleContent))
                       .sorted(Comparator.comparingInt(ModuleDto::getModuleOrderNumber))
                       .toList();
    }

    public ModuleDto mapModule(ModuleEntity entity, Boolean isNeedToFetchModuleContent) {
        return ModuleDto.builder()
                        .id(entity.getId())
                        .moduleTitle(entity.getModuleTitle())
                        .moduleOrderNumber(entity.getModuleOrderNumber())
                        .moduleContent(
                                isNeedToFetchModuleContent ? entity.getModuleContent() : null)
                        .isActive(entity.getIsActive())
                        .createdAt(entity.getCreatedAt())
                        .createdAt(entity.getCreatedAt())
                        .build();
    }

    public UserInfoDto mapUserDto(UserEntity entity) {
        return UserInfoDto.builder()
                          .id(entity.getId())
                          .username(entity.getUsername())
                          .role(entity.getRole())
                          .firstName(entity.getFirstName())
                          .lastName(entity.getLastName())
                          .tgNickname(entity.getTgNickname())
                          .build();

    }

    /**
     * Преобразует DTO информации о пользователе в сущность пользователя.
     *
     * @param userInfoDto
     *         DTO информации о пользователе для преобразования
     *
     * @return сущность пользователя
     */
    public UserEntity mapUserEntity(UserInfoDto userInfoDto) {
        return UserEntity.builder()
                         .id(userInfoDto.getId())
                         .username(userInfoDto.getUsername())
                         .role(userInfoDto.getRole())
                         .firstName(userInfoDto.getFirstName())
                         .lastName(userInfoDto.getLastName())
                         .tgNickname(userInfoDto.getTgNickname())
                         .tgChatId(userInfoDto.getTgChatId())
                         .build();
    }

    /**
     * Создать gRPC-объект для запроса страницы любых объектов (аналог {@link PageRequest})
     *
     * @param requestId
     *         сквозной UUID запроса
     * @param pageNumber
     *         номер страницы
     * @param pageSize
     *         размер страницы
     *
     * @return gRPC-объект {@link GrpcPageRequest}
     */
    public GrpcPageRequest constructGrpcPageRequest(
            String requestId,
            int pageNumber,
            int pageSize) {
        return GrpcPageRequest.newBuilder()
                              .setRequestId(requestId)
                              .setPageNumber(pageNumber)
                              .setPageSize(pageSize)
                              .build();
    }

    /**
     * Преобразовать gRPC-объект в {@link PageRequest}
     *
     * @param pageDetails
     *         rRPC-объект {@link PageDetails}
     *
     * @return {@link PageRequest}
     */
    public PageRequest mapGrpcPageDetailsToPageRequest(PageDetails pageDetails) {
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
    public PageRequest mapGrpcPageRequestToPageRequest(GrpcPageRequest grpcPageRequest) {
        return PageRequest.of(grpcPageRequest.getPageNumber(), grpcPageRequest.getPageSize());
    }

    public GetAllModulesRequest constructGetAllModulesRequest(String requestId, long courseId) {
        return GetAllModulesRequest.newBuilder()
                                   .setRequestId(requestId)
                                   .setCourseId(courseId)
                                   .build();
    }

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
