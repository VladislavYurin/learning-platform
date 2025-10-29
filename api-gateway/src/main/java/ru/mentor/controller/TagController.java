package ru.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.gateway.api.TagControllerApi;
import ru.mentor.gateway.model.CourseTagDto;
import ru.mentor.mapper.CourseTagDtoMapper;
import ru.mentor.services.RedirectTagService;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagController implements TagControllerApi {

    private final RedirectTagService redirectTagService;
    private final CourseTagDtoMapper courseTagDtoMapper;

    /**
     * Реализация ручки GET /course-tag/all
     */
    @Override
    public ResponseEntity<List<CourseTagDto>> getAllTags() {
        List<ru.mentor.dto.tag.CourseTagDto> listCommonCourseTagDto = redirectTagService.getAllTags().getBody();
        List<CourseTagDto> listApiCourseTagDto = courseTagDtoMapper.toListApiDto(listCommonCourseTagDto);
        return ResponseEntity.ok(listApiCourseTagDto);
    }
}
