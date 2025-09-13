package ru.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.SubmoduleEntity;

@Repository
public interface SubmoduleRepository extends JpaRepository<SubmoduleEntity, Long> {
}
