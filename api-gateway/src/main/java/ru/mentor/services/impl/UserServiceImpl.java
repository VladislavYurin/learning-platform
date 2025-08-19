package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.UserException;
import ru.mentor.repository.UserRepository;
import ru.mentor.services.UserService;

/**
 * Реализация доменного сервиса пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Создаёт нового пользователя, если в системе нет пользователя с именем {@code username}.
     * Проверяет уникальность по username, при отсутствии конфликтов сохраняет сущность.
     * @param userEntity доменная сущность пользователя для сохранения
     * @return сохраненная сущность (с заполненным идентификатором)
     * @throws UserException если пользователь с таким username уже существует
     */
    @Override
    public UserEntity create(UserEntity userEntity) {
        if (!userRepository.existsByUsername(userEntity.getUsername())) {
            return userRepository.save(userEntity);
        }
        throw new UserException(String.format(
                "Юзер с username = %s уже существует",
                userEntity.getUsername()
        ));
    }

    /**
     * Проверяет существование пользователя по имени пользователя.
     * @param username имя пользователя для проверки (не {@code null})
     * @return true, если пользователь с таким именем существует, иначе false
     */
    @Override
    public boolean existsByUserName(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Возвращает пользователя по имени или кидает исключение, если не найден.
     * @param username имя пользователя
     * @return найденная сущность пользователя
     */
    @Override
    public UserEntity getByUsername(String username) {
        return userRepository.findByUsernameOrThrow(username);
    }

    /**
     * Возвращает адаптер для интеграции со Spring Security.
     * @return адаптер, который загружает пользователя по {@code username}
     */
    @Override
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Возвращает текущего аутентифицированного пользователя из контекста безопасности.
     * @return сущность текущего пользователя
     */
    @Override
    public UserEntity getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    /**
     * Возвращает идентификатор текущего пользователя из principal контекста безопасности.
     * @return идентификатор пользователя
     */
    @Override
    public Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserEntity userEntity) {
            return userEntity.getId();
        }
        throw new IllegalStateException("Пользователь не найден в контексте безопасности");
    }

}
