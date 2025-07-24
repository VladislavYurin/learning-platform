package ru.mentor.services;

import ru.mentor.dto.UserInfoDto;

public interface UserInfoService {

    UserInfoDto getMyUserInfo();

    UserInfoDto getOtherUserInfo(Long userId);

    UserInfoDto assignMentorRole();

}
