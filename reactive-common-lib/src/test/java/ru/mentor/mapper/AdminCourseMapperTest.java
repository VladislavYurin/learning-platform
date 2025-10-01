package ru.mentor.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

class AdminCourseMapperTest {

    private final UserMapper userMapper = new UserMapper();
    private final AdminCourseMapper mapper = new AdminCourseMapper(userMapper);

    @Test
    void mapCourseEntityToGrpcCourseResponse_returnsExpectedResponse() {
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        UserEntity courseAuthor = TestEntityStubGenerator.constructAuthorUserEntity();

        CourseResponse response = mapper.mapCourseEntityToGrpcCourseResponse(
                courseEntity,
                courseAuthor
        );

        Assertions.assertEquals(TestConstantHolder.COURSE_ID, response.getCourseId());
        Assertions.assertEquals(TestConstantHolder.COURSE_TITLE, response.getTitle());
        Assertions.assertEquals(TestConstantHolder.COURSE_DESCRIPTION, response.getDescription());
        Assertions.assertEquals(TestConstantHolder.IS_ACTIVE_COURSE, response.getIsActive());
        Assertions.assertEquals(
                TestConstantHolder.CREATED_AT_EPOCH_SECONDS,
                response.getCreatedAt().getSeconds()
        );
        Assertions.assertEquals(TestConstantHolder.MENTOR_ID, response.getAuthor().getUserId());
        Assertions.assertEquals(TestConstantHolder.USERNAME, response.getAuthor().getUsername());
        Assertions.assertEquals(TestConstantHolder.FIRST_NAME, response.getAuthor().getFirstName());
        Assertions.assertEquals(TestConstantHolder.LAST_NAME, response.getAuthor().getLastName());
        Assertions.assertEquals(
                TestConstantHolder.TG_NICKNAME,
                response.getAuthor().getTgNickname()
        );
        Assertions.assertEquals(TestConstantHolder.TG_CHAT_ID, response.getAuthor().getTgChatId());
    }

    @Test
    void mapCourseResponsePageToGrpcAllCoursesResponse_returnsExpectedAggregation() {
        Page<CourseResponse> page = TestGrpcStubGenerator.constructCourseResponsePage();

        AllCoursesResponse aggregated = mapper.mapCourseResponsePageToGrpcAllCoursesResponse(page);
        CourseResponse expectedCourse = page.getContent().get(TestConstantHolder.PAGE_NUMBER);

        Assertions.assertEquals(page.getContent().size(), aggregated.getCoursesCount());
        Assertions.assertEquals(
                expectedCourse,
                aggregated.getCourses(TestConstantHolder.PAGE_NUMBER)
        );
        Assertions.assertEquals(page.getNumber(), aggregated.getPageDetails().getPage());
        Assertions.assertEquals(page.getSize(), aggregated.getPageDetails().getSize());
        Assertions.assertEquals(
                page.getTotalElements(),
                aggregated.getPageDetails().getTotalElements()
        );
        Assertions.assertEquals(page.getTotalPages(), aggregated.getPageDetails().getTotalPages());
    }

}
