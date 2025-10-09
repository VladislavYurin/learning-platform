package ru.mentor.services;

import org.springframework.http.ResponseEntity;
import ru.mentor.dto.tag.CourseTagDto;
import java.util.List;

public interface RedirectTagService {

    ResponseEntity<List<CourseTagDto>> getAllTags();
}
