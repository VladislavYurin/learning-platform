package ru.mentor.mapper;

import java.util.List;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.Header;
import ru.mentor.common.PageDetails;
import ru.mentor.dto.CourseDto;
import ru.mentor.entity.CourseEntity;

@Mapper(componentModel = "spring",
        uses = {AdminModuleMapper.class,
                BaseMapper.class,
                TagGrpcMapper.class,
                UserMapper.class,
                UtilMapper.class},
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminCourseMapper {

    /**
     * Преобразует gRPC-объект в DTO для отправки пользователю.
     *
     * @param response
     *         {@link CourseResponse} объект курса из gRPC-клиента.
     *
     * @return {@link CourseDto} данные о курсе.
     */
    @Mapping(target = "id", source = "courseId")
    @Mapping(target = "courseTitle", source = "title")
    @Mapping(target = "courseDescription", source = "description")
    @Mapping(target = "createdAt",
            qualifiedByName = "timestampToLocalDateTime")
    @Mapping(target = "author",
            qualifiedByName = "mapGrpcAuthorResponseToUserInfoDto")
    @Mapping(target = "tags",
            qualifiedByName = "toCourseTagDto")
    CourseDto mapGrpcCourseResponseToCourseDto(CourseResponse response);

    /**
     * Преобразует gRPC-объект в DTO для отправки пользователю
     *
     * @param grpcCoursesResponse
     *         gRPC-объект, содержащий список курсов
     *
     * @return объект {@link Page}, содержащий объекты {@link CourseDto}
     */
    default Page<CourseDto> mapGrpcCourseResponseToCourseDtoPage(AllCoursesResponse grpcCoursesResponse) {

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
    @Mapping(target = "courseId", source = "id")
    @Mapping(target = "title", source = "courseTitle")
    @Mapping(target = "createdAt",
            qualifiedByName = "buildTimestamp")
    @Mapping(target = "author", source = "courseEntity.author",
            qualifiedByName = "mapUserEntityToCourseAuthorResponse")
    CourseResponse mapCourseEntityToGrpcCourseResponse(CourseEntity courseEntity);

    /**
     * Преобразует объект {@link Page} в gRPC-объект
     *
     * @param coursesPage
     *         {@link Page}
     *
     * @return gRPC-объект {@link AllCoursesResponse}
     */
    default AllCoursesResponse mapCourseEntityPageToGrpcAllCoursesResponse
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
    @Mapping(target = "senderId", ignore = true)
    GetCourseRequest constructGetCourseRequest(Header header, long courseId);

    private PageDetails extractPageDetailsFromCourseEntityPage(Page<CourseEntity> coursesPage) {
        return PageDetails.newBuilder()
                          .setPage(coursesPage.getNumber())
                          .setSize(coursesPage.getSize())
                          .setTotalElements(coursesPage.getTotalElements())
                          .setTotalPages(coursesPage.getTotalPages())
                          .build();
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
