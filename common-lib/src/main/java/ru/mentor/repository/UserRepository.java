package ru.mentor.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityNotFoundException;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    default UserEntity findByUsernameOrThrow(String username){
        return this.findByUsername(username)
                   .orElseThrow(() -> new EntityNotFoundException(
                           String.format(
                                   "Юзер с username = %s не найден",
                                   username
                           )
                   ));
    }

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
