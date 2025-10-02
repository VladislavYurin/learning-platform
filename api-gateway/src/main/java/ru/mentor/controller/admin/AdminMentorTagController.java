package ru.mentor.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.mentorTag.MentorTagAttachResponseDto;
import ru.mentor.dto.mentorTag.MentorTagDetachRequestDto;
import ru.mentor.dto.mentorTag.MentorTagDetachResponseDto;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.dto.mentorTag.MentorTagDtoCreateRequest;
import ru.mentor.dto.mentorTag.MentorTagsAttachRequestDto;
import ru.mentor.services.RedirectAdminMentorTagService;

/**
 * Контроллер управления тэгами ментора для администратора.
 */
@RestController
@RequestMapping("/admin/mentor-tags")
@RequiredArgsConstructor
@Tag(name = "Admin mentor tags management", description = "Управление тэнами ментора для админов.")
public class AdminMentorTagController {
    private final RedirectAdminMentorTagService adminMentorTagService;

    @Operation(
            summary = "Получить все теги менторов",
            description = "Возвращает список всех тегов менторов",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список всех тегов менторов",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MentorTagDto.class)))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    @SecurityRequirement(name = "BearerAuthentication")
    public ResponseEntity<List<MentorTagDto>> getAllMentorTags() {
        return ResponseEntity.ok(adminMentorTagService.allMentorTags());
    }

    @Operation(
            summary = "Добавить свой тэг ментора",
            description = "Добавление собственного тэга ментора",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Добавленный тэг ментора",
                            content = @Content(schema = @Schema(implementation = MentorTagDto.class))),
                    @ApiResponse(responseCode = "401", description = "Не авторизирован")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    @SecurityRequirement(name = "BearerAuthentication")
    public ResponseEntity<MentorTagDto> createCustomMentorTag(@RequestBody MentorTagDtoCreateRequest request){
        return ResponseEntity.ok(adminMentorTagService.createCustomMentorTag(request));
    }

    @Operation(
            summary = "Привязать теги к ментору",
            description = "Привязать теги к ментору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список привязанных тегов",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MentorTagDto.class)))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет доступа"),
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/attach")
    @SecurityRequirement(name = "BearerAuthentication")
    public ResponseEntity<MentorTagAttachResponseDto> attachMentorTags(@RequestBody MentorTagsAttachRequestDto attachRequest){
        return ResponseEntity.ok(adminMentorTagService.attachMentorTags(attachRequest));
    };

    @Operation(
            summary = "Отвязать тег от ментора",
            description = "Отвязать тег от ментора",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Отвязанный тег",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MentorTagDto.class)))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет доступа"),
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/detach")
    @SecurityRequirement(name = "BearerAuthentication")
    public ResponseEntity<MentorTagDetachResponseDto> detachMentorTags(@RequestBody MentorTagDetachRequestDto detachRequest){
        return ResponseEntity.ok(adminMentorTagService.detachMentorTag(detachRequest));
    };
}
