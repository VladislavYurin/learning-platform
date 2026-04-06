package ru.mentor.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.mentor.common.AuthorResponse;
import ru.mentor.constant.Role;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.UserEntity;

class UserMapperTest {

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void mapGrpcAuthorResponseToUserInfoDto_mapsFields_andSetsMentorRole() {
        AuthorResponse author = AuthorResponse.newBuilder()
                .setUserId(42L)
                .setUsername("mentor1")
                .setFirstName("Ivan")
                .setLastName("Petrov")
                .setTgNickname("@ivan")
                .setTgChatId(99L)
                .build();

        UserInfoDto dto = mapper.mapGrpcAuthorResponseToUserInfoDto(author);

        Assertions.assertEquals(42L, dto.getId());
        Assertions.assertEquals("mentor1", dto.getUsername());
        Assertions.assertEquals(Role.MENTOR, dto.getRole());
        Assertions.assertEquals("Ivan", dto.getFirstName());
        Assertions.assertEquals("Petrov", dto.getLastName());
        Assertions.assertEquals("@ivan", dto.getTgNickname());
        Assertions.assertEquals(99L, dto.getTgChatId());
    }

    @Test
    void mapUserEntityToCourseAuthorResponse_mapsAllFields() {
        UserEntity user = UserEntity.builder()
                .id(10L)
                .username("author")
                .firstName("Ada")
                .lastName("Lovelace")
                .tgNickname("@ada")
                .tgChatId(777L)
                .build();

        AuthorResponse response = mapper.mapUserEntityToCourseAuthorResponse(user);

        Assertions.assertEquals(10L, response.getUserId());
        Assertions.assertEquals("author", response.getUsername());
        Assertions.assertEquals("Ada", response.getFirstName());
        Assertions.assertEquals("Lovelace", response.getLastName());
        Assertions.assertEquals("@ada", response.getTgNickname());
        Assertions.assertEquals(777L, response.getTgChatId());
    }
}
