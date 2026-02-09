package ru.mentor.mapper;

import com.google.protobuf.Timestamp;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.PageDetails;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;

/**
 * Converters between course entities and gRPC responses used by admin flows.
 */
@Component
@RequiredArgsConstructor
public class AdminCourseMapper {

    private final UserMapper userMapper;

    private final TagMapper tagMapper;

    private final AdminModuleMapper moduleMapper;

    private final UtilMapper utilMapper;

    public CourseResponse toCourseResponse(
            CourseEntity courseEntity,
            @Nullable UserEntity courseAuthor,
            @Nullable List<CourseTagEntity> tagsList,
            @Nullable List<ModuleEntity> modulesList) {

        Timestamp createdAtTimestamp = utilMapper.localDateTimeToTimestamp(courseEntity.getCreatedAt());

        CourseResponse.Builder builder = CourseResponse.newBuilder()
                .setCourseId(courseEntity.getId())
                .setTitle(courseEntity.getCourseTitle())
                .setDescription(courseEntity.getDescription())
                .setIsActive(courseEntity.getIsActive())
                .setCreatedAt(createdAtTimestamp);

        if (courseAuthor != null) {
            builder.setAuthor(userMapper.userEntityToAuthorResponse(
                    courseAuthor));
        }
        if (tagsList != null) {
            builder.addAllTags(tagsList.stream()
                    .map(tagMapper::courseTagEntityToCourseTagResponse)
                    .toList());
        }
        if (modulesList != null) {
            builder.addAllModules(modulesList.stream()
                    .map(moduleMapper::mapModuleEntityToModuleResponse)
                    .toList());
        }

        return builder.build();
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