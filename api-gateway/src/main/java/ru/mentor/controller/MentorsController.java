package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.common.UserInfo;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.feign.MentorClient;
import ru.mentor.util.RqGenerator;

import java.util.List;

@RestController
@RequestMapping("/mentors")
@RequiredArgsConstructor
@Tag(name = "Список всех менторов")
public class MentorsController {

    private final MentorClient mentorClient;

    @GetMapping
    @Operation(summary = "Получить список всех менторов")
    public ResponseEntity<List<UserInfoDto>> getAllMentors() {
        String requestId = RqGenerator.generateRqId();
        return mentorClient.getAllMentors(requestId);
    }
}
