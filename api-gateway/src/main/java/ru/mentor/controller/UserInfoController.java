package ru.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.gateway.api.UserInfoControllerApi;
import ru.mentor.gateway.model.UserInfoDto;
import ru.mentor.mapper.UserInfoDtoMapper;
import ru.mentor.services.UserInfoService;

/**
 * Контроллер для управления информацией пользователя.
 * Предоставляет эндпоинты для получения данных о текущем пользователе,
 * просмотра профиля другого пользователя по идентификатору и назначения
 * роли наставника текущему пользователю.
 */
@RestController
@RequiredArgsConstructor
public class UserInfoController implements UserInfoControllerApi {

    private final UserInfoService userInfoService;
    private final UserInfoDtoMapper userInfoDtoMapper;

    /**
     * Реализация ручки POST /user/mentor/register
     */
    @Override
    public ResponseEntity<UserInfoDto> assignMentorRole() {
        ru.mentor.dto.UserInfoDto commonUserInfoDto = userInfoService.assignMentorRole();
        return ResponseEntity.ok().body(userInfoDtoMapper.toApiDto(commonUserInfoDto));
    }

    /**
     * Реализация ручки GET /user/me
     */
    @Override
    public ResponseEntity<UserInfoDto> getMyUserInfo() {
        ru.mentor.dto.UserInfoDto commonUserInfoDto = userInfoService.getMyUserInfo();
        return ResponseEntity.ok().body(userInfoDtoMapper.toApiDto(commonUserInfoDto));
    }

    /**
     * Реализация ручки GET /user/{userId}
     */
    @Override
    public ResponseEntity<UserInfoDto> getOtherUserInfo(Long userId) {
        ru.mentor.dto.UserInfoDto commonUserInfoDto = userInfoService.getOtherUserInfo(userId);
        return ResponseEntity.ok().body(userInfoDtoMapper.toApiDto(commonUserInfoDto));
    }
}