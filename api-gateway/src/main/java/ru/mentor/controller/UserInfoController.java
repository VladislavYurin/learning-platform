package ru.mentor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.services.UserInfoService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Получение информации о пользователе")
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> getMyUserInfo() {
        UserInfoDto response = userInfoService.getMyUserInfo();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDto> getOtherUserInfo(@PathVariable Long userId) {
        UserInfoDto response = userInfoService.getOtherUserInfo(userId);
        return ResponseEntity.ok().body(response);
    }

}
