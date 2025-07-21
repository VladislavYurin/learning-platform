package ru.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.EntityNotFoundException;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

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
