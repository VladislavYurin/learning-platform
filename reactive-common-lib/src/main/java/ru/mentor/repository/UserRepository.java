package ru.mentor.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityNotFoundException;

/**
 * Репозиторий для работы с сущностями пользователей.
 * Предоставляет методы для выполнения CRUD операций и дополнительные методы
 * для поиска пользователей по различным критериям.
 */
@Repository
public interface UserRepository extends R2dbcRepository<UserEntity, Long> {

    /**
     * Проверяет, существует ли пользователь с указанным именем пользователя.
     *
     * @param username
     *         имя пользователя
     *
     * @return true, если пользователь существует, иначе false
     */
    Mono<Boolean> existsByUsername(String username);

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username
     *         имя пользователя
     *
     * @return Optional с сущностью пользователя или пустой Optional, если пользователь не найден
     */
    Mono<UserEntity> findByUsername(String username);

    /**
     * Находит пользователя по имени пользователя или выбрасывает исключение, если пользователь не
     * найден.
     *
     * @param username
     *         имя пользователя
     *
     * @return сущность пользователя
     *
     * @throws EntityNotFoundException
     *         если пользователь с указанным именем не найден
     */
    default Mono<UserEntity> findByUsernameOrThrow(String username) {
        return this.findByUsername(username)
                   .switchIfEmpty(Mono.error(new EntityNotFoundException(
                           String.format(
                                   "Юзер с username = %s не найден",
                                   username
                           )
                   )));
    }

    /**
     * Находит пользователя по его идентификатору или выбрасывает исключение, если пользователь не
     * найден.
     *
     * @param userId
     *         идентификатор пользователя
     *
     * @return сущность пользователя
     *
     * @throws EntityNotFoundException
     *         если пользователь с указанным идентификатором не найден
     */
    default Mono<UserEntity> findByIdOrThrow(Long userId) {
        return this.findById(userId)
                   .switchIfEmpty(Mono.error(new EntityNotFoundException(
                           String.format(
                                   "Юзер с ID = %d не найден",
                                   userId
                           )
                   )));
    }

    @Query("""
            SELECT
            *
            FROM users JOIN mentor_time_slot__users slots_users
                ON users.id_user = slots_users.user_id
            WHERE slots_users.time_slot_id = :slotId""")
    Flux<UserEntity> findAllSlotParticipantsBySlotId(Long slotId);


    Flux<UserEntity> findAllById(Long id);
}
