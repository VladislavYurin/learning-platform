package ru.mentor.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.Course;
import ru.mentor.dto.InnerCreateCourseRequest;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.AccessDeniedException;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.CourseService;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final BaseMapper baseMapper;

    @Override
    public Course createCourse(InnerCreateCourseRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                String.format(
                                                        "Юзер с ID = %d не найден",
                                                        request.getUserId()
                                                )
                                        ));
        if (Role.checkIsMentor(user) || Role.checkIsAdmin(user)) {
            CourseEntity course = CourseEntity.builder()
                                              .author(user)
                                              .name(request.getCourseName())
                                              .description(request.getCourseDescription())
                                              .build();

            CourseEntity courseEntity = courseRepository.save(course);
            return baseMapper.mapCourse(courseEntity, false);
        } else {
            throw new AccessDeniedException(
                    String.format(
                            "Юзер с ID = %d не имеет доступа к созданию курса",
                            request.getUserId()
                    )
            );
        }
    }

    @Override
    public void deleteCourse(Long userId, Long courseId) {
        UserEntity deletedByUser = userRepository.findById(userId)
                                                 .orElseThrow(() -> new EntityNotFoundException(
                                                         String.format(
                                                                 "Юзер с ID = %d не найден",
                                                                 userId
                                                         )
                                                 ));

        CourseEntity course = courseRepository.findById(courseId)
                                              .orElseThrow(() -> new EntityNotFoundException(
                                                      String.format(
                                                              "Курс с ID = %d не найден",
                                                              courseId
                                                      )
                                              ));

        // Админ может удалять любой курс
        if (Role.checkIsAdmin(deletedByUser)) {
            courseRepository.deleteById(courseId);
            return;
        }

        // Ментор или автор курса может удалять только свои курсы
        if (Role.checkIsMentor(deletedByUser) || course.getAuthor().equals(deletedByUser)) {
            courseRepository.deleteById(courseId);
            return;
        }

        // Если дошли сюда — доступ запрещён
        throw new AccessDeniedException(
                String.format(
                        "Юзер с ID = %d не имеет доступа к удалению курса %d",
                        userId,
                        courseId
                )
        );
    }

    @Override
    public List<Course> getAllActiveCourses() {
        List<CourseEntity> courses = courseRepository.findAllByIsActiveTrue();
        return baseMapper.mapCourses(courses, false);
    }

    @Override
    public List<Course> getAllCourses() {
        List<CourseEntity> courses = courseRepository.findAll();
        return baseMapper.mapCourses(courses, false);
    }

    @Override
    public Course getCourseById(Long courseId) {
        CourseEntity course = courseRepository.findById(courseId)
                                              .orElseThrow(() -> new EntityNotFoundException(
                                                      String.format(
                                                              "Курс с ID = %d не найден",
                                                              courseId
                                                      )
                                              ));
        return baseMapper.mapCourse(course, true);
    }

}
