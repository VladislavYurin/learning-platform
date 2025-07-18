package ru.mentor.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentor.dto.Course;
import ru.mentor.dto.Module;
import ru.mentor.dto.Question;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.QuestionEntity;

@Component
@RequiredArgsConstructor
public class BaseMapper {

    public List<Course> mapCourses(
            List<CourseEntity> entities,
            Boolean isNeedToFetchInnerEntities) {
        return entities.stream()
                       .map(entity -> mapCourse(entity, isNeedToFetchInnerEntities))
                       .toList();
    }

    public Course mapCourse(CourseEntity entity, Boolean isNeedToFetchInnerEntities) {
        return Course.builder()
                     .id(entity.getId())
                     .courseName(entity.getName())
                     .courseDescription(entity.getDescription())
                     .isActive(entity.getIsActive())
                     .authorId(entity.getAuthor().getId())
                     .modules(isNeedToFetchInnerEntities ? mapModules(entity.getModules()) : null)
                     .build();
    }

    public List<Module> mapModules(List<ModuleEntity> entities) {
        return entities.stream().map(this::mapModule).toList();
    }

    public Module mapModule(ModuleEntity entity) {
        return Module.builder()
                     .id(entity.getId())
                     .moduleName(entity.getName())
                     .moduleDescription(entity.getDescription())
                     .isActive(entity.getIsActive())
                     .createdAt(entity.getCreatedAt())
                     .questions(mapQuestions(entity.getQuestions()))
                     .build();
    }

    public List<Question> mapQuestions(List<QuestionEntity> entities) {
        return entities.stream().map(this::mapQuestion).toList();
    }

    public Question mapQuestion(QuestionEntity entity) {
        return Question.builder()
                       .id(entity.getId())
                       .questionText(entity.getQuestionText())
                       .answerText(entity.getAnswerText())
                       .createdAt(entity.getCreatedAt())
                       .build();
    }

}
