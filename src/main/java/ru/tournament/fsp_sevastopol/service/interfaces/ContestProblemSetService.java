package ru.tournament.fsp_sevastopol.service.interfaces;

import org.springframework.security.oauth2.jwt.Jwt;
import ru.tournament.fsp_sevastopol.dto.problemset.BindProblemSetToContestRequestDto;
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetShortResponseDto;

public interface ContestProblemSetService {

    ProblemSetShortResponseDto bindProblemSetToContest(
            Long contestId,
            BindProblemSetToContestRequestDto dto,
            Long currentUserId
    );

    ProblemSetShortResponseDto getContestProblemSet(
            Long contestId,
            Long currentUserId
    );

    void unbindProblemSetFromContest(
            Long contestId,
            Long currentUserId
    );
}