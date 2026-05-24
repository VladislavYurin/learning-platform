package ru.mentor.mapper;

import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
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
@Mapper(componentModel = "spring",
        uses = {AdminModuleMapper.class,
                TagMapper.class,
                UserMapper.class,
                UtilMapper.class},
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminCourseMapper {

    @Mapping(target = "courseId", source = "courseEntity.id")
    @Mapping(target = "title", source = "courseEntity.courseTitle")
    @Mapping(target = "description", source = "courseEntity.description")
    @Mapping(target = "isActive", source = "courseEntity.isActive")
    @Mapping(target = "createdAt", source = "courseEntity.createdAt",
            qualifiedByName = "buildTimestamp")
    @Mapping(target = "author", source = "courseAuthor",
            qualifiedByName = "mapUserEntityToCourseAuthorResponse",
            conditionExpression = "java(courseAuthor != null)")
    @Mapping(target = "tags", source = "tagsList",
            qualifiedByName = "toGrpcTagResponse",
            conditionExpression = "java(tagsList != null)")
    @Mapping(target = "modules", source = "modulesList",
            qualifiedByName = "mapModuleEntityToModuleResponse",
            conditionExpression = "java(modulesList != null)")
    CourseResponse mapCourseEntityToGrpcCourseResponse(
            CourseEntity courseEntity,
            @Nullable UserEntity courseAuthor,
            @Nullable List<CourseTagEntity> tagsList,
            @Nullable List<ModuleEntity> modulesList);

    default AllCoursesResponse mapCourseResponsePageToGrpcAllCoursesResponse(
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
