package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.AuthorResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.Header;
import ru.mentor.common.PageDetails;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.entity.CourseEntity;

@Component
@RequiredArgsConstructor
public class AdminCourseMapper {

    private final UserMapper userMapper;

    private final AdminModuleMapper moduleMapper;

    private final TagGrpcMapper tagGrpcMapper;

    /**
     * Преобразует gRPC-объект в DTO для отправки пользователю.
     *
     * @param response
     *         {@link CourseResponse} объект курса из gRPC-клиента.
     *
     * @return {@link CourseDto} данные о курсе.
     */
    public CourseDto mapGrpcCourseResponseToCourseDto(CourseResponse response) {

        LocalDateTime createdAtDateTime = LocalDateTime.ofEpochSecond(
                response.getCreatedAt().getSeconds(),
                response.getCreatedAt().getNanos(),
                ZoneOffset.UTC
        );

        UserInfoDto authorInfo = userMapper.mapGrpcAuthorResponseToUserInfoDto(response.getAuthor());
        List<ModuleDto> moduleDtoList = moduleMapper.toModuleDtoList(response.getModulesList());
        List<CourseTagDto> tagsList =
                response.getTagsList()
                        .stream()
                        .map(tagGrpcMapper::fromGrpc)
                        .toList();

        return CourseDto.builder()
                        .id(response.getCourseId())
                        .courseTitle(response.getTitle())
                        .courseDescription(response.getDescription())
                        .isActive(response.getIsActive())
                        .createdAt(createdAtDateTime)
                        .author(authorInfo)
                        .modules(moduleDtoList)
                        .tags(tagsList)
                        .build();
    }

    /**
     * Преобразует gRPC-объект в DTO для отправки пользователю
     *
     * @param grpcCoursesResponse
     *         gRPC-объект, содержащий список курсов
     *
     * @return объект {@link Page}, содержащий объекты {@link CourseDto}
     */
    public Page<CourseDto> mapGrpcCourseResponseToCourseDtoPage(AllCoursesResponse grpcCoursesResponse) {

        List<CourseDto> courseDtoList = getDtoListFromAllCoursesResponse(grpcCoursesResponse);
        PageDetails pageDetails = grpcCoursesResponse.getPageDetails();

        return new PageImpl<>(
                courseDtoList,
                constructPageRequest(grpcCoursesResponse),
                pageDetails.getTotalElements()
        );
    }

    /**
     * Преобразует сущность курса в gRPC-объект
     *
     * @param courseEntity
     *         сущность курса
     *
     * @return gRPC-объект {@link CourseResponse}
     */
    public CourseResponse mapCourseEntityToGrpcCourseResponse(CourseEntity courseEntity) {
        Timestamp createdAtTimestamp = Timestamp.newBuilder()
                                                .setSeconds(courseEntity.getCreatedAt()
                                                                        .toEpochSecond(ZoneOffset.UTC))
                                                .build();
        return CourseResponse.newBuilder()
                             .setCourseId(courseEntity.getId())
                             .setTitle(courseEntity.getCourseTitle())
                             .setDescription(courseEntity.getDescription())
                             .setIsActive(courseEntity.getIsActive())
                             .setCreatedAt(createdAtTimestamp)
                             .setAuthor(getAuthorFromCourseEntity(courseEntity))
                             .build();
    }

    /**
     * Преобразует объект {@link Page} в gRPC-объект
     *
     * @param coursesPage
     *         {@link Page}
     *
     * @return gRPC-объект {@link AllCoursesResponse}
     */
    public AllCoursesResponse mapCourseEntityPageToGrpcAllCoursesResponse
    (Page<CourseEntity> coursesPage) {

        List<CourseResponse> courseResponses = coursesPage.getContent().stream()
                                                          .map(this::mapCourseEntityToGrpcCourseResponse)
                                                          .toList();
        return AllCoursesResponse.newBuilder()
                                 .setPageDetails(extractPageDetailsFromCourseEntityPage(coursesPage))
                                 .addAllCourses(courseResponses)
                                 .build();
    }

    /**
     * Создает gRPC-объект запроса курса {@link GetCourseRequest}
     *
     * @param header
     *         заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param courseId
     *         ID курса
     *
     * @return {@link GetCourseRequest}
     */
    public GetCourseRequest constructGetCourseRequest(Header header, long courseId) {
        return GetCourseRequest.newBuilder()
                               .setHeader(header)
                               .setCourseId(courseId)
                               .build();
    }

    private PageDetails extractPageDetailsFromCourseEntityPage(Page<CourseEntity> coursesPage) {
        return PageDetails.newBuilder()
                          .setPage(coursesPage.getNumber())
                          .setSize(coursesPage.getSize())
                          .setTotalElements(coursesPage.getTotalElements())
                          .setTotalPages(coursesPage.getTotalPages())
                          .build();
    }

    private AuthorResponse getAuthorFromCourseEntity(CourseEntity courseEntity) {
        return userMapper.mapUserEntityToCourseAuthorResponse(courseEntity.getAuthor());
    }

    private List<CourseDto> getDtoListFromAllCoursesResponse(AllCoursesResponse grpcCoursesResponse) {
        return grpcCoursesResponse.getCoursesList().stream()
                                  .map(this::mapGrpcCourseResponseToCourseDto)
                                  .toList();
    }

    private PageRequest constructPageRequest(AllCoursesResponse grpcCoursesResponse) {
        PageDetails pageDetails = grpcCoursesResponse.getPageDetails();
        return PageRequest.of(
                pageDetails.getPage(),
                pageDetails.getSize()
        );
    }

}
