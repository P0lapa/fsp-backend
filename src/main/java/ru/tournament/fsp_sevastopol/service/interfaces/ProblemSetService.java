package ru.tournament.fsp_sevastopol.service.interfaces;

import ru.tournament.fsp_sevastopol.dto.problemset.*;

import java.util.List;

public interface ProblemSetService {

    ProblemSetResponseDto createProblemSet(ProblemSetRequestDto dto, Long currentUserId);

    List<ProblemSetShortResponseDto> getAvailableProblemSets(Long currentUserId);

    ProblemSetResponseDto getProblemSetById(Long id, Long currentUserId);

    ProblemSetResponseDto updateProblemSet(Long id, ProblemSetRequestDto dto, Long currentUserId);

    void deleteProblemSet(Long id, Long currentUserId);

    ProblemSetTaskResponseDto addTaskToProblemSet(
            Long problemSetId,
            ProblemSetTaskRequestDto dto,
            Long currentUserId
    );

    ProblemSetTaskResponseDto updateProblemSetTask(
            Long problemSetId,
            Long linkId,
            ProblemSetTaskRequestDto dto,
            Long currentUserId
    );

    void removeTaskFromProblemSet(
            Long problemSetId,
            Long linkId,
            Long currentUserId
    );
}