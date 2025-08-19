package ru.mentor.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
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
     */
    boolean existsByUsername(String username);

    /**
     * Находит пользователя по имени пользователя.
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Находит пользователя по имени пользователя или выбрасывает исключение, если пользователь не найден.
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

}
