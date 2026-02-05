package ru.mentor.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.mentor.entity.UserEntity;

/**
 * Сервисный интерфейс для работы с пользователями.
 * Содержит методы для управления пользователями и получения информации о текущем пользователе.
 */
public interface UserService {

    /**
     * Создание нового пользователя.
     *
     * @param userEntity объект пользователя
     * @return созданный пользователь
     */
    UserEntity create(UserEntity userEntity);

    /**
     * Проверяет наличие пользователя по его имени.
     * @param username имя пользователя для проверки (не {@code null})
     * @return true, если пользователь с таким именем существует, иначе false
     */
    boolean existsByUserName(String username);

    /**
     * Получение пользователя по его имени.
     *
     * @param username имя пользователя
     * @return пользователь
     */
    UserEntity getByUsername(String username);

    /**
     * Возвращение реализацию {@link UserDetailsService}, основанную на методе получения пользователя по имени.
     *
     * @return реализация UserDetailsService
     */
    UserDetailsService userDetailsService();

    /**
     * Получение текущего аутентифицированного пользователя из контекста безопасности.
     *
     * @return текущий пользователь
     */
    UserEntity getCurrentUser();

    /**
     * Получение id текущего пользователя.
     *
     * @return id текущего пользователя
     */
    Long getCurrentUserId();

    /**
     * Получение пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return пользователь
     */
    UserEntity getUserById(Long userId);

}
