package ru.mentor.mapper;

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

import java.util.List;

@Mapper(componentModel = "spring",
        uses = UtilMapper.class,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminCourseMapper {

    /**
     * Преобразует gRPC-объект в DTO для отправки пользователю.
     *
     * @param response {@link CourseResponse} объект курса из gRPC-клиента.
     * @return {@link CourseDto} данные о курсе.
     */
    @Mapping(target = "id", source = "courseId")
    @Mapping(target = "courseTitle", source = "title")
    @Mapping(target = "courseDescription", source = "description")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "createdAt", source = "createdAt",
            qualifiedByName = "timestampToLocalDateTime")
    @Mapping(target = "author", source = "author",
            qualifiedByName = "authorResponseToUserInfoDto")
    CourseDto courseResponseToCourseDto(CourseResponse response);

    /**
     * Преобразует gRPC-объект в DTO для отправки пользователю
     *
     * @param grpcCoursesResponse gRPC-объект, содержащий список курсов
     * @return объект {@link Page}, содержащий объекты {@link CourseDto}
     */
    default Page<CourseDto> allCoursesResponseToCourseDtoPage(AllCoursesResponse grpcCoursesResponse) {

        List<CourseDto> courseDtoList = AllCoursesResponseToCourseDtoList(grpcCoursesResponse);
        PageDetails pageDetails = grpcCoursesResponse.getPageDetails();

        return new PageImpl<>(
                courseDtoList,
                AllCoursesResponseToPageRequest(grpcCoursesResponse),
                pageDetails.getTotalElements()
        );
    }

    /**
     * Преобразует сущность курса в gRPC-объект
     *
     * @param courseEntity сущность курса
     * @return gRPC-объект {@link CourseResponse}
     */
    @Mapping(target = "courseId", source = "id")
    @Mapping(target = "title", source = "courseTitle")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "createdAt", source = "createdAt",
            qualifiedByName = "localDateTimeToTimestamp")
    @Mapping(target = "author", source = "author",
            qualifiedByName = "userEntityToAuthorResponse")
    CourseResponse courseEntityToCourseResponse(CourseEntity courseEntity);

    /**
     * Преобразует объект {@link Page} в gRPC-объект
     *
     * @param coursesPage {@link Page}
     * @return gRPC-объект {@link AllCoursesResponse}
     */
    default AllCoursesResponse courseEntityPageToAllCoursesResponse
    (Page<CourseEntity> coursesPage) {

        List<CourseResponse> courseResponses = coursesPage.getContent().stream()
                .map(this::courseEntityToCourseResponse)
                .toList();
        return AllCoursesResponse.newBuilder()
                .setPageDetails(courseEntityPageToPageDetails(coursesPage))
                .addAllCourses(courseResponses)
                .build();
    }

    /**
     * Создает gRPC-объект запроса курса {@link GetCourseRequest}
     *
     * @param header   заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param courseId ID курса
     * @return {@link GetCourseRequest}
     */
    default GetCourseRequest toGetCourseRequest(Header header, long courseId) {
        return GetCourseRequest.newBuilder()
                .setHeader(header)
                .setCourseId(courseId)
                .build();
    }

    private PageDetails courseEntityPageToPageDetails(Page<CourseEntity> coursesPage) {
        return PageDetails.newBuilder()
                .setPage(coursesPage.getNumber())
                .setSize(coursesPage.getSize())
                .setTotalElements(coursesPage.getTotalElements())
                .setTotalPages(coursesPage.getTotalPages())
                .build();
    }

    private List<CourseDto> AllCoursesResponseToCourseDtoList(AllCoursesResponse grpcCoursesResponse) {
        return grpcCoursesResponse.getCoursesList().stream()
                .map(this::courseResponseToCourseDto)
                .toList();
    }

    private PageRequest AllCoursesResponseToPageRequest(AllCoursesResponse grpcCoursesResponse) {
        PageDetails pageDetails = grpcCoursesResponse.getPageDetails();
        return PageRequest.of(
                pageDetails.getPage(),
                pageDetails.getSize()
        );
    }

}
