package ru.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.ModuleEntity;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {
}
