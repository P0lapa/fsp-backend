package ru.tournament.fsp_sevastopol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tournament.fsp_sevastopol.entity.ProblemSetEntity;

import java.util.List;

public interface ProblemSetRepository extends JpaRepository<ProblemSetEntity, Long> {

    List<ProblemSetEntity> findByIsPublicTrueOrCreatedByUserId(Long createdByUserId);

    boolean existsByIdAndIsPublicTrueOrCreatedByUserId(Long id, Long createdByUserId);
}