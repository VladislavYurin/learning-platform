package ru.mentor.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.gateway.model.UserInfoDto;
import ru.mentor.gateway.api.AdminUserInfoControllerApi;
import ru.mentor.mapper.UserInfoDtoMapper;
import ru.mentor.services.UserInfoService;

/**
 * Контроллер для управления учетными записями пользователей для администраторов.
 */
@RestController
@RequiredArgsConstructor
public class AdminUserInfoController implements AdminUserInfoControllerApi {

    private final UserInfoService userInfoService;
    private final UserInfoDtoMapper userInfoDtoMapper;

    /**
     * Реализация ручки GET /admin/user/me
     */
    @Override
    public ResponseEntity<UserInfoDto> adminGetMyUserInfo() {
        ru.mentor.dto.UserInfoDto response = userInfoService.getMyUserInfo();
        return ResponseEntity.ok().body(userInfoDtoMapper.toApiDto(response));
    }

    /**
     * Реализация ручки GET /admin/user/{userId}
     */
    @Override
    public ResponseEntity<UserInfoDto> adminGetOtherUserInfo(Long userId) {
        ru.mentor.dto.UserInfoDto response = userInfoService.getOtherUserInfo(userId);
        return ResponseEntity.ok().body(userInfoDtoMapper.toApiDto(response));
    }
}
