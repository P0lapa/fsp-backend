package ru.tournament.fsp_sevastopol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tournament.fsp_sevastopol.entity.TaskEntity;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findByIsPublicTrueOrAuthorUserId(Long authorUserId);

    boolean existsByIdAndIsPublicTrueOrAuthorUserId(Long id, Long authorUserId);
}