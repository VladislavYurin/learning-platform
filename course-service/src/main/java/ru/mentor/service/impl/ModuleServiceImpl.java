package ru.mentor.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.constant.Role;
import ru.mentor.dto.InnerCreateModuleRequest;
import ru.mentor.dto.ModuleDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.CustomAccessDeniedException;
import ru.mentor.exception.FileProcessingException;
import ru.mentor.kafka.KafkaFacade;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.ModuleService;
import ru.mentor.util.AccessChecker;
import ru.mentor.util.MarkdownConverter;

/**
 * Реализация сервиса для управления модулями в системе управления онлайн-курсами.
 * Cервис предоставляет методы для создания, удаления и получения модулей,
 * а также управляет доступом к ним в соответствии с ролями пользователей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleServiceImpl implements ModuleService {

    /**
     * Множество допустимых типов содержимого для модуля.
     */
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

    private final KafkaFacade kafkaFacade;

    /**
     * Создает новый модуль в рамках указанного курса.
     *
     * @param request Запрос, содержащий информацию о модуля (название, порядок, содержание).
     * @return DTO модуля, который был создан.
     * @throws CustomAccessDeniedException Если у пользователя нет прав для добавления модуля в курс.
     */
    @Override
    public ModuleDto createModule(InnerCreateModuleRequest request) {

        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());

        // Проверяем права пользователя на создание модуля
        if (Role.checkIsAdmin(user) ||
                (Role.checkIsMentor(user) && Role.checkMentorIsAuthorOfCourse(user, course))) {
            ModuleEntity module = ModuleEntity.builder()
                                              .moduleTitle(request.getModuleTitle())
                                              .moduleOrderNumber(request.getModuleOrderNumber())
                                              .moduleContent(request.getModuleContent())
                                              .course(course)
                                              .isActive(true)
                                              .build();

            ModuleEntity moduleEntity = moduleRepository.save(module);
            UserEntity mentor = user;
            kafkaFacade.sendModuleCreatedMessage(course, moduleEntity, mentor, user);
            return baseMapper.mapModule(moduleEntity, false);
        } else {
            // Если пользователь не имеет прав доступа, выбрасываем исключение
            throw new CustomAccessDeniedException(
                    String.format(
                            "Юзер с ID = %d не имеет доступа к добавлению модуля в курс с ID = %d",
                            request.getUserId(),
                            request.getCourseId()
                    )
            );
        }

    }

    /**
     * Удаляет модуль по идентификатору.
     *
     * @param userId Идентификатор пользователя, инициирующего удаление модуля.
     * @param courseId Идентификатор курса, содержащего модуль.
     * @param moduleId Идентификатор удаляемого модуля.
     * @throws CustomAccessDeniedException Если у пользователя нет прав для удаления модуля.
     */
    @Override
    @Transactional
    public void deleteModule(Long userId, Long courseId, Long moduleId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        CourseEntity course = courseRepository.findByIdOrThrow(courseId);
        ModuleEntity module = moduleRepository.findByIdOrThrow(moduleId);

        // Проверяем права пользователя на удаление модуля
        if (Role.checkIsAdmin(user) ||
                (Role.checkIsMentor(user) && Role.checkMentorIsAuthorOfCourse(user, course))) {
            ModuleEntity moduleEntity = moduleRepository.findByIdOrThrow(moduleId);
            moduleRepository.delete(moduleEntity);
            kafkaFacade.sendModuleDeletedMessage(course, module, user);
        } else {
            // Если пользователь не имеет прав доступа, выбрасываем исключение
            throw new CustomAccessDeniedException(
                    String.format(
                            "Юзер с ID = %d не имеет доступа к удалению модуля с ID = %d в курсе с ID = %d",
                            userId,
                            moduleId,
                            courseId
                    )
            );
        }
    }

    /**
     * Получает модуль по его идентификатору.
     *
     * @param userId Идентификатор пользователя, запрашивающего модуль.
     * @param courseId Идентификатор курса, которому принадлежит модуль.
     * @param moduleId Идентификатор запрашиваемого модуля.
     * @return DTO модуля, соответствующего запрашиваемому идентификатору.
     * @throws CustomAccessDeniedException Если у пользователя нет доступа к модулю.
     */
    @Override
    public ModuleDto getModuleById(Long userId, Long courseId, Long moduleId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        CourseEntity course = courseRepository.findByIdOrThrow(courseId);
        ModuleEntity module = moduleRepository.findByIdOrThrow(moduleId);

        // Проверяем права доступа
        if (Role.checkIsAdmin(user) ||
                (Role.checkIsMentor(user) && Role.checkMentorIsAuthorOfCourse(user, course)) ||
                (accessChecker.hasAccessToCourse(userId, courseId) &&
                        accessChecker.hasAccessToModule(userId, moduleId))) {
            return baseMapper.mapModule(module, true);
        }
        // Если пользователь не имеет прав доступа, выбрасываем исключение
        throw new CustomAccessDeniedException(
                String.format(
                        "Юзер с ID = %d не имеет доступа к модулю с ID = %d",
                        userId,
                        moduleId
                )
        );
    }

    /**
     * Импортирует модуль из файла, предоставленного пользователем.
     *
     * @param request Запрос, содержащий информацию о модуле (название, порядок).
     * @param file Файл, содержащий содержимое модуля в формате Markdown.
     * @return DTO импортированного модуля, который был создан.
     * @throws CustomAccessDeniedException Если у пользователя нет прав на импорт модуля.
     * @throws FileProcessingException Если происходит ошибка при чтении файла.
     */
    @Override
    public ModuleDto importModuleFromFile(
            InnerCreateModuleRequest request,
            MultipartFile file) {
        UserEntity user = userRepository.findByIdOrThrow(request.getUserId());
        CourseEntity course = courseRepository.findByIdOrThrow(request.getCourseId());

        // Проверяем права доступа пользователя для импорта модуля
        if (!Role.checkIsAdmin(user) &&
                !(Role.checkIsMentor(user) && Role.checkMentorIsAuthorOfCourse(user, course))) {
            throw new CustomAccessDeniedException("Нет прав на импорт модулей");
        }
        try {
            // Читаем содержимое файла и конвертируем его в HTML
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
