package ru.mentor.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentor.common.AuthorResponse;
import ru.mentor.common.Role;
import ru.mentor.common.UserInfo;
import ru.mentor.entity.UserEntity;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;

@SpringBootTest(classes = {
        UserMapperImpl.class,
        UtilMapperImpl.class
})
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void mapUserEntityToCourseAuthorResponse_returnsExpectedAuthor() {
        UserEntity userEntity = TestEntityStubGenerator.constructAuthorUserEntity();
        userEntity.setId(TestConstantHolder.COURSE_AUTHOR_ID);

        AuthorResponse response = userMapper.userEntityToAuthorResponse(userEntity);

        Assertions.assertEquals(TestConstantHolder.MENTOR_ID, response.getUserId());
        Assertions.assertEquals(TestConstantHolder.USERNAME, response.getUsername());
        Assertions.assertEquals(TestConstantHolder.FIRST_NAME, response.getFirstName());
        Assertions.assertEquals(TestConstantHolder.LAST_NAME, response.getLastName());
        Assertions.assertEquals(TestConstantHolder.TG_NICKNAME, response.getTgNickname());
        Assertions.assertEquals(TestConstantHolder.TG_CHAT_ID, response.getTgChatId());
    }

    @Test
    void mapUserEntityToUserInfo_returnsExpectedUserInfo() {
        UserEntity userEntity = TestEntityStubGenerator.constructParticipantEntity();

        UserInfo userInfo = userMapper.userEntityToUserInfo(userEntity);

        Assertions.assertEquals(TestConstantHolder.USER_ID, userInfo.getId());
        Assertions.assertEquals(TestConstantHolder.USERNAME, userInfo.getUsername());
        Assertions.assertEquals(Role.USER, userInfo.getRole());
        Assertions.assertEquals(TestConstantHolder.FIRST_NAME, userInfo.getFirstName());
        Assertions.assertEquals(TestConstantHolder.LAST_NAME, userInfo.getLastName());
        Assertions.assertEquals(TestConstantHolder.TG_NICKNAME, userInfo.getTgNickname());
        Assertions.assertEquals(TestConstantHolder.TG_CHAT_ID, userInfo.getTgChatId());
    }

}
