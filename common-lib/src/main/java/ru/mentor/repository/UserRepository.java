package ru.mentor.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.constant.Role;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityNotFoundException;

/**
 * Репозиторий для работы с сущностями пользователей.
 * Предоставляет методы для выполнения CRUD операций и дополнительные методы
 * для поиска пользователей по различным критериям.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Проверяет, существует ли пользователь с указанным именем пользователя.
     *
     * @param username имя пользователя
     * @return true, если пользователь существует, иначе false
     */
    boolean existsByUsername(String username);

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя
     * @return Optional с сущностью пользователя или пустой Optional, если пользователь не найден
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Находит пользователя по имени пользователя или выбрасывает исключение, если пользователь не найден.
     *
     * @param username имя пользователя
     * @return сущность пользователя
     * @throws EntityNotFoundException если пользователь с указанным именем не найден
     */
    default UserEntity findByUsernameOrThrow(String username){
        return this.findByUsername(username)
                   .orElseThrow(() -> new EntityNotFoundException(
                           String.format(
                                   "Юзер с username = %s не найден",
                                   username
                           )
                   ));
    }

    /**
     * Находит пользователя по его идентификатору или выбрасывает исключение, если пользователь не найден.
     *
     * @param userId идентификатор пользователя
     * @return сущность пользователя
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    default UserEntity findByIdOrThrow(Long userId) {
        return this.findById(userId)
                   .orElseThrow(() -> new EntityNotFoundException(
                           String.format(
                                   "Юзер с ID = %d не найден",
                                   userId
                           )
                   ));
    }

    /**
     * Проверяет, существует ли пользователь с указанным ID и ролью.
     *
     * @param userId идентификатор пользователя
     * @param userRole роль (enum Role)
     * @return true, если пользователь с такой ролью существует, иначе false
     */
    boolean existsByIdAndRole(Long userId, Role userRole);

}
