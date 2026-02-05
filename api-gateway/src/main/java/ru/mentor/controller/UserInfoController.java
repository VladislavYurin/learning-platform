package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.avatar.UserAvatarContentDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.services.UserAvatarService;
import ru.mentor.services.UserInfoService;
import ru.mentor.services.UserService;

/**
 * Контроллер для управления информацией пользователя и его аватаром.
 * Предоставляет эндпоинты для получения/изменения данных текущего пользователя,
 * просмотра профиля другого пользователя, назначения роли наставника,
 * а также получения и обновления аватара.
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Получение или изменение информации о пользователе")
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final UserService userService;
    private final UserAvatarService userAvatarService;

    @Operation(
            summary = "Получить свою информацию",
            description = "Позволяет получить пользовательскую информацию от пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользовательская информация выдана"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @GetMapping("/me")
    public ResponseEntity<?> getMyUserInfo() {
        UserInfoDto response = userInfoService.getMyUserInfo();
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Получить информацию о другом пользователе",
            description = "Позволяет получить пользовательскую информацию о другом пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользовательская информация выдана"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<?> getOtherUserInfo(@PathVariable Long userId) {
        UserInfoDto response = userInfoService.getOtherUserInfo(userId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Выдает пользователю роль МЕНТОР",
            description = "Позволяет выдать пользователю роль МЕНТОР",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Роль МЕНТОР выдана"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @PostMapping("/mentor/register")
    public ResponseEntity<?> assignMentorRole() {
        UserInfoDto response = userInfoService.assignMentorRole();
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Изменить свою информацию",
            description = "Позволяет изменить пользовательскую информацию от пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользовательская информация изменена"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @PutMapping("/me")
    public ResponseEntity<?> updateMyUserInfo(@RequestBody UserInfoDto updateDto) {
        UserInfoDto response = userInfoService.updateMyUserInfo(updateDto);
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Получить свой аватар",
            description = "Возвращает файл аватара текущего пользователя. " +
                    "По умолчанию отображается в браузере, при download=true — скачивается.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Аватар найден и возвращён"),
                    @ApiResponse(responseCode = "404", description = "Аватар не найден")
            }
    )
    @GetMapping("/me/avatar")
    public ResponseEntity<Resource> getMyUserAvatar(
            @RequestParam(name = "download", required = false, defaultValue = "false") boolean download
    ) {
        UserEntity user = userService.getCurrentUser();
        return buildUserAvatarResponse(user.getUserAvatarKey(), download);
    }

    @Operation(
            summary = "Получить аватар пользователя по id",
            description = "Возвращает файл аватара пользователя по userId. " +
                    "По умолчанию отображается в браузере, при download=true — скачивается.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Аватар найден и возвращён"),
                    @ApiResponse(responseCode = "404", description = "Пользователь или аватар не найден")
            }
    )
    @GetMapping("/{userId}/avatar")
    public ResponseEntity<Resource> getOtherUserAvatar(
            @PathVariable Long userId,
            @RequestParam(name = "download", required = false, defaultValue = "false") boolean download
    ) {
        UserEntity user = userService.getUserById(userId);
        return buildUserAvatarResponse(user.getUserAvatarKey(), download);
    }

    @Operation(
            summary = "Обновить (загрузить) свой аватар",
            description = "Загружает аватар текущего пользователя в MinIO. Если аватар уже был — заменяет.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Аватар успешно загружен/обновлён"),
                    @ApiResponse(responseCode = "400", description = "Некорректный файл аватара")
            }
    )
    @PutMapping(
            value = "/me/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> updateMyUserAvatar(
            @RequestPart("avatar")
            MultipartFile avatar
    ) {
        userInfoService.updateMyUserAvatar(avatar);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<Resource> buildUserAvatarResponse(
            String userAvatarKey,
            boolean download) {
        Optional<UserAvatarContentDto> avatarOpt =
                userAvatarService.getUserAvatarFromStorage(userAvatarKey);

        if (avatarOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserAvatarContentDto avatar = avatarOpt.get();

        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(avatar.getContentType());
        } catch (Exception ignore) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        ContentDisposition contentDisposition = (download
                ? ContentDisposition.attachment()
                : ContentDisposition.inline())
                .filename(avatar.getFilename())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDisposition(contentDisposition);

        if (avatar.getSize() != null && avatar.getSize() >= 0) {
            headers.setContentLength(avatar.getSize());
        }

        InputStreamResource resource =
                new InputStreamResource(avatar.getInputStream());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(resource);
    }

}