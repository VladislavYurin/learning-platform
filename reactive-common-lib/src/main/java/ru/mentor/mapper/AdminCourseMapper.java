package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import java.time.ZoneOffset;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.PageDetails;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;

/**
 * Converters between course entities and gRPC responses used by admin flows.
 */
@Component
public class AdminCourseMapper {

    private final UserMapper userMapper;

    public AdminCourseMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public CourseResponse mapCourseEntityToGrpcCourseResponse(
            CourseEntity courseEntity,
            UserEntity courseAuthor) {
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

    public AllCoursesResponse mapCourseResponsePageToGrpcAllCoursesResponse(
            Page<CourseResponse> courseResponsePage) {
        return AllCoursesResponse.newBuilder()
                                 .setPageDetails(extractPageDetailsFromCourseResponsePage(
                                         courseResponsePage))
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

}
