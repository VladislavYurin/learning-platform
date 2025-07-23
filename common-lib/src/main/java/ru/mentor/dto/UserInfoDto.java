package ru.mentor.dto;

import lombok.Builder;
import lombok.Data;
import ru.mentor.constant.Role;

@Data
@Builder
public class UserInfoDto {

    private Long id;

    private String username;

    private Role role;

    private String firstName;

    private String lastName;

    private String tgNickname;

}
