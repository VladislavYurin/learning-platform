package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.PageDetails;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;

// НЕ УДАЛЯТЬ, НУЖЕН ДЛЯ РЕАКТИВЩИНЫ
@Component
@RequiredArgsConstructor
public class AdminCourseMapper {

    private final UserMapper userMapper;

    private static int MIN_SIZE = 0;

    /**
     * Преобразует сущность курса в gRPC-объект
     *
     * @param courseEntity
     *         сущность курса
     *
     * @return gRPC-объект {@link CourseResponse}
     */
    public CourseResponse mapCourseEntityToGrpcCourseResponse(CourseEntity courseEntity, UserEntity courseAuthor) {
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
                .setAuthor(userMapper.mapUserEntityToCourseAuthorResponse(courseAuthor))
                .build();
    }

        /**
         * Преобразует объект {@link Page} в gRPC-объект
         *
         * @param courseResponsePage
         *         {@link Page}
         *
         * @return gRPC-объект {@link AllCoursesResponse}
         */
        public AllCoursesResponse mapCourseResponsePageToGrpcAllCoursesResponse
        (Page<CourseResponse> courseResponsePage) {
            return AllCoursesResponse.newBuilder()
                                     .setPageDetails(extractPageDetailsFromCourseResponsePage(courseResponsePage))
                                     .addAllCourses(courseResponsePage.stream().toList())
                                     .build();
        }

    private PageDetails extractPageDetailsFromCourseResponsePage(Page<CourseResponse> coursesPage) {
        return PageDetails.newBuilder()
                          .setPage(coursesPage.getNumber())
                          .setSize(coursesPage.getSize())
                          .setTotalElements(coursesPage.getTotalElements())
                          .setTotalPages(coursesPage.getTotalPages())
                          .build();
    }

//    /**
//     * Преобразует gRPC-объект в DTO для отправки пользователю.
//     *
//     * @param response
//     *         {@link CourseResponse} объект курса из gRPC-клиента.
//     *
//     * @return {@link CourseDto} данные о курсе.
//     */
//    public CourseDto mapGrpcCourseResponseToCourseDto(CourseResponse response) {
//
//        LocalDateTime createdAtDateTime = LocalDateTime.ofEpochSecond(
//                response.getCreatedAt().getSeconds(),
//                response.getCreatedAt().getNanos(),
//                ZoneOffset.UTC
//        );
//
//        UserInfoDto authorInfo = userMapper.mapGrpcAuthorResponseToUserInfoDto(response.getAuthor());
//
//        return CourseDto.builder()
//                        .id(response.getCourseId())
//                        .courseTitle(response.getTitle())
//                        .courseDescription(response.getDescription())
//                        .isActive(response.getIsActive())
//                        .createdAt(createdAtDateTime)
//                        .author(authorInfo)
//                        .build();
//    }
//
//    /**
//     * Преобразует gRPC-объект в DTO для отправки пользователю
//     *
//     * @param grpcCoursesResponse
//     *         gRPC-объект, содержащий список курсов
//     *
//     * @return объект {@link Page}, содержащий объекты {@link CourseDto}
//     */
//    public Page<CourseDto> mapGrpcCourseResponseToCourseDtoPage(AllCoursesResponse grpcCoursesResponse) {
//
//        List<CourseDto> courseDtoList = getDtoListFromAllCoursesResponse(grpcCoursesResponse);
//        PageDetails pageDetails = grpcCoursesResponse.getPageDetails();
//
//        return new PageImpl<>(
//                courseDtoList,
//                constructPageRequest(grpcCoursesResponse),
//                pageDetails.getTotalElements()
//        );
//    }
//
//
//    /**
//     * Создает gRPC-объект запроса курса {@link GetCourseRequest}
//     *
//     * @param requestId
//     *         сквозной UUID запроса
//     * @param courseId
//     *         ID курса
//     *
//     * @return {@link GetCourseRequest}
//     */
//    public GetCourseRequest constructGetCourseRequest(String requestId, long courseId) {
//        return GetCourseRequest.newBuilder()
//                               .setRequestId(requestId)
//                               .setCourseId(courseId)
//                               .build();
//    }
//

//
//    private AuthorResponse getAuthorFromCourseEntity(CourseEntity courseEntity) {
//        return userMapper.mapUserEntityToCourseAuthorResponse(courseEntity.getAuthor());
//    }
//
//    private List<CourseDto> getDtoListFromAllCoursesResponse(AllCoursesResponse grpcCoursesResponse) {
//        return grpcCoursesResponse.getCoursesList().stream()
//                                  .map(this::mapGrpcCourseResponseToCourseDto)
//                                  .toList();
//    }
//
//    private PageRequest constructPageRequest(AllCoursesResponse grpcCoursesResponse) {
//        PageDetails pageDetails = grpcCoursesResponse.getPageDetails();
//        return PageRequest.of(
//                pageDetails.getPage(),
//                pageDetails.getSize()
//        );
//    }

}
