package ru.mentor.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.InnerCreateModuleRequest;
import ru.mentor.dto.ModuleDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.AccessDeniedException;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.ModuleService;
import ru.mentor.util.AccessChecker;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    private final BaseMapper baseMapper;

    private final AccessChecker accessChecker;

    @Override
    public ModuleDto createModule(InnerCreateModuleRequest request) {

        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());

        if (Role.checkIsAdmin(user) ||
                (Role.checkIsMentor(user) && Role.checkMentorIsAuthorOfCourse(user, course))) {
            ModuleEntity module = ModuleEntity.builder()
                                              .moduleTitle(request.getModuleTitle())
                                              .moduleOrderNumber(request.getModuleOrderNumber())
                                              .description(request.getModuleDescription())
                                              .course(course)
                                              .build();

            ModuleEntity moduleEntity = moduleRepository.save(module);
            return baseMapper.mapModule(moduleEntity, false);
        } else {
            throw new AccessDeniedException(
                    String.format(
                            "Юзер с ID = %d не имеет доступа к добавлению модуля в курс с ID = %d",
                            request.getUserId(),
                            request.getCourseId()
                    )
            );
        }

    }

    @Override
    public void deleteModule(Long userId, Long courseId, Long moduleId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        CourseEntity course = courseRepository.findByIdOrThrow(courseId);
        if (Role.checkIsAdmin(user) ||
                (Role.checkIsMentor(user) && Role.checkMentorIsAuthorOfCourse(user, course))) {
            ModuleEntity moduleEntity = moduleRepository.findByIdOrThrow(moduleId);
            moduleRepository.delete(moduleEntity);
        } else {
            throw new AccessDeniedException(
                    String.format(
                            "Юзер с ID = %d не имеет доступа к удалению модуля с ID = %d в курсе с ID = %d",
                            userId,
                            moduleId,
                            courseId
                    )
            );
        }
    }

    @Override
    public ModuleDto getModuleById(Long userId, Long courseId, Long moduleId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        CourseEntity course = courseRepository.findByIdOrThrow(courseId);
        ModuleEntity module = moduleRepository.findByIdOrThrow(moduleId);
        if (Role.checkIsAdmin(user) ||
                (Role.checkIsMentor(user) && Role.checkMentorIsAuthorOfCourse(user, course)) ||
                (accessChecker.hasAccessToCourse(userId, courseId) &&
                        accessChecker.hasAccessToModule(userId, moduleId))) {
            return baseMapper.mapModule(module, true);
        }
        throw new AccessDeniedException(
                String.format(
                        "Юзер с ID = %d не имеет доступа к модулю с ID = %d",
                        userId,
                        moduleId
                )
        );
    }

}
