package ru.mentor.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.CourseProgressStatisticDto;
import ru.mentor.dto.MenteeProgressDto;
import ru.mentor.exception.CustomAccessDeniedException;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.ModuleRepository;
import ru.mentor.repository.UserCourseAccessRepository;
import ru.mentor.repository.UserModuleAccessRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.ProgressService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Реализация сервиса для получения информации о прогрессе учащихся в курсах.
 * Предоставляет методы для получения прогресса курса и статистики по учащимся.
 */
@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final CourseRepository courseRepository;

    private final ModuleRepository moduleRepository;

    private final UserCourseAccessRepository userCourseAccessRepository;

    private final UserModuleAccessRepository userModuleAccessRepository;

    private final UserRepository userRepository;

    /**
     * Получает прогресс курса для заданного наставника.
     *
     * @param mentorId Идентификатор наставника, которому принадлежит курс.
     * @param courseId Идентификатор курса, для которого требуется получить прогресс.
     * @return Объект CourseProgressResponse, содержащий сведения о прогрессе учащихся в курсе.
     * @throws EntityNotFoundException Если курс или наставник не найдены.
     */
    @Override
    public CourseProgressResponse getCourseProgressByMentor(Long mentorId, Long courseId) {
        UserEntity mentor = userRepository.findByIdOrThrow(mentorId);
        CourseEntity course = courseRepository.findByIdOrThrow(courseId);

        // 1. Проверяем, что наставник является автором курса
        if (Role.checkMentorIsAuthorOfCourse(mentor, course) || Role.checkIsAdmin(mentor)) {

            // 2. Получаем всех учеников курса
            List<UserCourseAccessEntity> courseAccesses = userCourseAccessRepository.findAllByCourseId(
                    courseId);

            // 3. Получаем все модули курса, отсортированные по порядку
            List<ModuleEntity> courseModules = moduleRepository.findAllByCourseIdOrderByModuleOrderNumberAsc(
                    courseId);

            // 4. Собираем данные по каждому ученику
            List<MenteeProgressDto> mentees = new ArrayList<>();
            Map<Integer, Integer> moduleDistribution = new HashMap<>();

            for (UserCourseAccessEntity access : courseAccesses) {
                UserEntity student = access.getUser();

                // Получаем все модули, к которым есть доступ у ученика
                List<UserModuleAccessEntity> studentModuleAccesses = userModuleAccessRepository
                        .findAllByUserIdAndCourseId(student.getId(), courseId);

                // Находим модуль с максимальным порядковым номером
                Long currentModuleId = studentModuleAccesses.stream()
                                                            .map(access1 -> access1.getModule()
                                                                                   .getModuleOrderNumber())
                                                            .max(Integer::compare)
                                                            .flatMap(maxOrder -> courseModules.stream()
                                                                                              .filter(module -> module.getModuleOrderNumber()
                                                                                                                      .equals(maxOrder))
                                                                                              .findFirst()
                                                                                              .map(ModuleEntity::getId))
                                                            .orElse(null);

                // Обновляем статистику
                if (currentModuleId != null) {
                    int moduleOrder = moduleRepository.findById(currentModuleId)
                                                      .orElseThrow()
                                                      .getModuleOrderNumber();
                    moduleDistribution.merge(moduleOrder, 1, Integer::sum);
                }

                mentees.add(MenteeProgressDto.builder()
                                             .userId(student.getId())
                                             .firstName(student.getFirstName())
                                             .lastName(student.getLastName())
                                             .currentModuleId(currentModuleId)
                                             .tgNickname(student.getTgNickname())
                                             .build());
            }

            // 5. Формируем статистику
            CourseProgressStatisticDto statistic = CourseProgressStatisticDto.builder()
                                                                             .totalMenteeCount(
                                                                                     mentees.size())
                                                                             .moduleDistribution(
                                                                                     moduleDistribution)
                                                                             .build();

            // 6. Собираем итоговый ответ
            return CourseProgressResponse.builder()
                                         .courseId(course.getId())
                                         .courseTitle(course.getCourseTitle())
                                         .mentee(mentees)
                                         .statistic(statistic)
                                         .build();
        } else {
            // Если юзер не является автором курса, выбрасываем исключение
            throw new CustomAccessDeniedException(
                    String.format(
                            "Юзер с ID = %d не является автором курса с ID = %d",
                            mentorId,
                            courseId
                    )
            );
        }
    }

    /**
     * Получает список всех пользователей, участвующих в указанном курсе под руководством заданного наставника.
     *
     * @param mentorId Идентификатор наставника, который проводит курс.
     * @param courseId Идентификатор курса, для которого требуется получить список пользователей.
     * @return Список объектов MenteeProgressDto, представляющих пользователей, участвующих в курсе.
     * @throws EntityNotFoundException Если курс или наставник не найдены.
     * @throws CustomAccessDeniedException Если наставник не является автором курса или не имеет достаточных прав.
     */
    @Override
    public List<MenteeProgressDto> getAllUsersAtCourse(Long mentorId, Long courseId) {
        UserEntity mentor = userRepository.findByIdOrThrow(mentorId);
        Role.checkUserIsAdminOrMentor(mentor);
        CourseEntity course = courseRepository.findByIdOrThrow(courseId);

        // 1. Проверяем, что наставник является автором курса
        if (Role.checkMentorIsAuthorOfCourse(mentor, course) || Role.checkIsAdmin(mentor)) {

            // 2. Получаем всех учеников курса
            List<UserCourseAccessEntity> courseAccesses = userCourseAccessRepository.findAllByCourseId(
                    courseId);

            // 3. Получаем все модули курса, отсортированные по порядку
            List<ModuleEntity> courseModules = moduleRepository.findAllByCourseIdOrderByModuleOrderNumberAsc(
                    courseId);

            // 4. Собираем данные по каждому ученику
            List<MenteeProgressDto> mentees = new ArrayList<>();

            for (UserCourseAccessEntity access : courseAccesses) {
                UserEntity student = access.getUser();

                // Получаем все модули, к которым есть доступ у ученика
                List<UserModuleAccessEntity> studentModuleAccesses = userModuleAccessRepository
                        .findAllByUserIdAndCourseId(student.getId(), courseId);

                // Находим модуль с максимальным порядковым номером
                Long currentModuleId = studentModuleAccesses.stream()
                                                            .map(access1 -> access1.getModule()
                                                                                   .getModuleOrderNumber())
                                                            .max(Integer::compare)
                                                            .flatMap(maxOrder -> courseModules.stream()
                                                                                              .filter(module -> module.getModuleOrderNumber()
                                                                                                                      .equals(maxOrder))
                                                                                              .findFirst()
                                                                                              .map(ModuleEntity::getId))
                                                            .orElse(null);

                mentees.add(MenteeProgressDto.builder()
                                             .userId(student.getId())
                                             .firstName(student.getFirstName())
                                             .lastName(student.getLastName())
                                             .tgNickname(student.getTgNickname())
                                             .currentModuleId(currentModuleId)
                                             .build());
            }

            return mentees;
        } else {
            // Если юзер не является автором курса, выбрасываем исключение
            throw new CustomAccessDeniedException(
                    String.format(
                            "Юзер с ID = %d не является автором курса с ID = %d",
                            mentorId,
                            courseId
                    )
            );
        }
    }

}
