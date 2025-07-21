package ru.mentor.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentor.repository.UserCourseAccessRepository;
import ru.mentor.repository.UserModuleAccessRepository;

@Component
@RequiredArgsConstructor
public class AccessChecker {

    private final UserCourseAccessRepository userCourseAccessRepository;

    private final UserModuleAccessRepository userModuleAccessRepository;

    public boolean hasAccessToCourse(Long userId, Long courseId) {
        return userCourseAccessRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    public boolean hasAccessToModule(Long userId, Long moduleId) {
        return userModuleAccessRepository.existsByUserIdAndModuleId(userId, moduleId);
    }

}
