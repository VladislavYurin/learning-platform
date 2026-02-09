package ru.mentor.mapper;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@SpringBootTest(classes = {
        AdminCourseMapper.class,
        AdminModuleMapperImpl.class,
        TagMapperImpl.class,
        UserMapperImpl.class,
        UtilMapperImpl.class
})
class AdminCourseMapperTest {

    @Autowired
    private AdminCourseMapper mapper;

    @Test
    void mapCourseEntityToGrpcCourseResponse_returnsExpectedResponse() {
        final int TAGS_NUMBER = 4;
        final int MODULES_COUNT = 1;
        CourseEntity courseEntity = TestEntityStubGenerator.constructCourseEntity();
        courseEntity.setId(TestConstantHolder.COURSE_ID);

        UserEntity courseAuthor = TestEntityStubGenerator.constructAuthorUserEntity();
        courseAuthor.setId(TestConstantHolder.COURSE_AUTHOR_ID);

        ModuleEntity moduleEntity = TestEntityStubGenerator.constructModuleEntity();
        moduleEntity.setId(TestConstantHolder.MODULE_ID);

        List<CourseTagEntity> listOfTags =
                TestEntityStubGenerator.constructCourseTagEntityList(TAGS_NUMBER);
        List<ModuleEntity> listOfModules =
                List.of(moduleEntity);

        CourseResponse response = mapper.toCourseResponse(
                courseEntity,
                courseAuthor,
                listOfTags,
                listOfModules
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

        Assertions.assertEquals(TAGS_NUMBER, response.getTagsCount());
        Assertions.assertEquals("test-tag-1", response.getTags(0).getName());
        Assertions.assertEquals(MODULES_COUNT, response.getModulesCount());
        Assertions.assertEquals(TestConstantHolder.MODULE_TITLE,
                response.getModulesList().get(0).getTitle());
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
