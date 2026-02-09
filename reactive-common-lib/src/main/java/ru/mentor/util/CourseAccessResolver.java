package ru.mentor.util;

import java.util.Map;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.mentor.constant.Role;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.repository.CourseRepository;

/**
 * Компонент реализует выбор стратегии получения курсов на основе роли пользователя.
 */
@Component
@RequiredArgsConstructor
public class CourseAccessResolver {

    private final CourseRepository courseRepository;
    private Map<Role, Function<UserEntity, Flux<CourseEntity>>> strategies;

    @PostConstruct
    public void initStrategies() {
        this.strategies = Map.of(
                Role.ADMIN, user -> courseRepository.findAll(),
                Role.MENTOR, user -> courseRepository.findAllByAuthorId(user.getId()),
                Role.USER, user -> courseRepository.findAllByUserAccess(user.getId())
        );
    }

    /**
     * Возвращает поток курсов, соответствующий роли пользователя,
     * либо пустой поток, если роль не определена в стратегии.
     * @param user - сущность пользователя
     * @return Flux<CourseEntity> - поток курсов, доступных пользователю согласно его роли.
     */
    public Flux<CourseEntity> resolveCoursesForUser(UserEntity user) {
        return strategies
                .getOrDefault(user.getRole(), u -> Flux.empty())
                .apply(user);
    }
}
