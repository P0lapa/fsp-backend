package ru.tournament.fsp_sevastopol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tournament.fsp_sevastopol.entity.ContestProblemSetEntity;

import java.util.Optional;
import java.util.List;

public interface ContestProblemSetRepository extends JpaRepository<ContestProblemSetEntity, Long> {

    Optional<ContestProblemSetEntity> findByContestId(Long contestId);

    List<ContestProblemSetEntity> findByProblemSetId(Long problemSetId);

    boolean existsByContestId(Long contestId);

    boolean existsByProblemSetId(Long problemSetId);

    void deleteByContestId(Long contestId);
}