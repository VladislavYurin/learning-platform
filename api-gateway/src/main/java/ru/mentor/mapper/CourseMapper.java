package ru.mentor.mapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllActiveCoursesResponse;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.GetAllActiveCoursesPreviewRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.front.CreateCourseRequest;
import ru.mentor.dto.tag.CourseTagDto;

/**
 * Маппер для формирования внутреннего запроса на создание курса и модуля.
 */
@Mapper(componentModel = "spring",
        uses = {AdminCourseMapper.class,
                BaseMapper.class,
                TagGrpcMapper.class,
                UserMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public abstract class CourseMapper {
    @Autowired
    protected BaseMapper baseMapper;

    @Autowired
    protected AdminCourseMapper adminCourseMapper;
    @Autowired
    protected TagGrpcMapper tagGrpcMapper;
    @Autowired
    protected UserMapper userMapper;

    @Mapping(target = "header", source = "header")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "courseName", source = "request.courseName")
    @Mapping(target = "courseDescription", source = "request.courseDescription")
    @Mapping(target = "tagIds", source = "request.tagIds")
    public abstract CreateCourseGrpcRequest constructGrpcCreateRequest(Header header, Long userId, CreateCourseRequest request);

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "userId")
    @Mapping(target = "courseId", source = "courseId")
    public abstract GetCourseRequest constructGrpcGetRequest(Header header, Long userId, Long courseId);

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "userId")
    public abstract GetAllActiveCoursesPreviewRequest constructGetAllActiveCoursesPreviewRequest(Header header, Long userId);

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "userId")
    @Mapping(target = "courseId", source = "courseId")
    public abstract DeleteCourseRequest constructGrpcDeleteRequest(Header header, Long userId, Long courseId);

    public GrpcPageRequest constructGrpcPageRequest(Header header, int pageNumber, int pageSize, Long userId) {
        return baseMapper.constructGrpcPageRequest(header, pageNumber, pageSize, userId);
    }

    public CourseDto mapGrpcCourseResponseToCourseDto(CourseResponse courseResponse) {
        return adminCourseMapper.mapGrpcCourseResponseToCourseDto(courseResponse);
    }

    public Page<CourseDto> mapGrpcCourseResponseToCourseDtoPage(AllCoursesResponse allActiveCourses) {
        return adminCourseMapper.mapGrpcCourseResponseToCourseDtoPage(allActiveCourses);
    }

    public List<CourseDto> mapGrpcAllActiveCoursesResponseToCourseDtoList(AllActiveCoursesResponse courses) {
        return courses.getCoursesList()
                      .stream()
                      .sorted(Comparator.comparing(CourseResponse::getTitle))
                      .map(this::mapCourseResponseToDto)
                      .toList();
    }

    public CourseDto mapCourseResponseToDto(CourseResponse course) {
        List<CourseTagDto> tagsList =
                course.getTagsList()
                      .stream()
                      .map(tagGrpcMapper::fromGrpc)
                      .toList();

        return CourseDto.builder()
                        .id(course.getCourseId())
                        .courseTitle(course.getTitle())
                        .courseDescription(course.getDescription())
                        .isActive(true)
                        .createdAt(LocalDateTime.ofEpochSecond(
                                course.getCreatedAt().getSeconds(),
                                course.getCreatedAt().getNanos(),
                                ZoneOffset.UTC))
                        .author(userMapper.mapGrpcAuthorResponseToUserInfoDto(
                                course.getAuthor()))
                        .tags(tagsList)
                        .build();
    }

}
