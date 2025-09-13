package ru.mentor.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.constant.Role;
import ru.mentor.dto.InnerCreateModuleRequest;
import ru.mentor.dto.ModuleDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.AccessDeniedException;
import ru.mentor.exception.FileProcessingException;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.ModuleService;
import ru.mentor.util.AccessChecker;
import ru.mentor.util.MarkdownConverter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleServiceImpl implements ModuleService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "text/markdown",
            "text/x-markdown",
            "application/octet-stream"
    );

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
                                              .moduleContent(request.getModuleContent())
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

    @Override
    public ModuleDto importModuleFromFile(
            InnerCreateModuleRequest request,
            MultipartFile file) {
        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());
        if (!Role.checkIsAdmin(user) &&
                !(Role.checkIsMentor(user) && Role.checkMentorIsAuthorOfCourse(user, course))) {
            throw new AccessDeniedException("Нет прав на импорт модулей");
        }
        try {
            String markdownContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            String htmlContent = MarkdownConverter.markdownToHtml(markdownContent);

            ModuleEntity module = ModuleEntity.builder()
                                              .moduleTitle(request.getModuleTitle())
                                              .moduleOrderNumber(request.getModuleOrderNumber())
                                              .moduleContent(htmlContent)
                                              .course(course)
                                              .build();
            ModuleEntity savedModule = moduleRepository.save(module);

            return baseMapper.mapModule(savedModule, true);
        } catch (IOException e) {
            log.error("Ошибка чтения файла", e);
            throw new FileProcessingException("Не удалось прочитать файл");
        }
    }

}
