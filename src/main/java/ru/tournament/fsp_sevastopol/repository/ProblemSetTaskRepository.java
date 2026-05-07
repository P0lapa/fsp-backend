package ru.tournament.fsp_sevastopol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tournament.fsp_sevastopol.entity.ProblemSetTaskEntity;

import java.util.List;
import java.util.Optional;

public interface ProblemSetTaskRepository extends JpaRepository<ProblemSetTaskEntity, Long> {

    List<ProblemSetTaskEntity> findByProblemSetIdOrderByOrderNumAsc(Long problemSetId);

    Optional<ProblemSetTaskEntity> findByIdAndProblemSetId(Long id, Long problemSetId);

    boolean existsByProblemSetId(Long problemSetId);

    boolean existsByTaskId(Long taskId);

    long countByProblemSetId(Long problemSetId);

    boolean existsByProblemSetIdAndTaskId(Long problemSetId, Long taskId);

    boolean existsByProblemSetIdAndOrderNum(Long problemSetId, Integer orderNum);

    boolean existsByProblemSetIdAndContestLabel(Long problemSetId, String contestLabel);

    boolean existsByProblemSetIdAndTaskIdAndIdNot(Long problemSetId, Long taskId, Long id);

    boolean existsByProblemSetIdAndOrderNumAndIdNot(Long problemSetId, Integer orderNum, Long id);

    boolean existsByProblemSetIdAndContestLabelAndIdNot(Long problemSetId, String contestLabel, Long id);

    void deleteByIdAndProblemSetId(Long id, Long problemSetId);

    void deleteByProblemSetId(Long problemSetId);
}