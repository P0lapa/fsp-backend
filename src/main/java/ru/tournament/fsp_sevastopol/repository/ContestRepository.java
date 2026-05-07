package ru.tournament.fsp_sevastopol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tournament.fsp_sevastopol.entity.ContestEntity;

import java.util.List;

public interface ContestRepository extends JpaRepository<ContestEntity, Long> {

    List<ContestEntity> findByIsPublicTrue();
}
