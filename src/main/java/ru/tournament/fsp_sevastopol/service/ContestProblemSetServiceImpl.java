package ru.tournament.fsp_sevastopol.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.tournament.fsp_sevastopol.dto.problemset.BindProblemSetToContestRequestDto;
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetShortResponseDto;
import ru.tournament.fsp_sevastopol.entity.ContestEntity;
import ru.tournament.fsp_sevastopol.entity.ContestProblemSetEntity;
import ru.tournament.fsp_sevastopol.entity.ProblemSetEntity;
import ru.tournament.fsp_sevastopol.enums.ContestStatusEnum;
import ru.tournament.fsp_sevastopol.mapper.ProblemSetMapper;
import ru.tournament.fsp_sevastopol.repository.ContestProblemSetRepository;
import ru.tournament.fsp_sevastopol.repository.ContestRepository;
import ru.tournament.fsp_sevastopol.repository.ProblemSetRepository;
import ru.tournament.fsp_sevastopol.repository.ProblemSetTaskRepository;
import ru.tournament.fsp_sevastopol.service.interfaces.ContestProblemSetService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContestProblemSetServiceImpl implements ContestProblemSetService {

    private final ContestRepository contestRepository;
    private final ProblemSetRepository problemSetRepository;
    private final ProblemSetTaskRepository problemSetTaskRepository;
    private final ContestProblemSetRepository contestProblemSetRepository;

    private final ProblemSetMapper problemSetMapper;

    @Override
    @Transactional
    public ProblemSetShortResponseDto bindProblemSetToContest(
            Long contestId,
            BindProblemSetToContestRequestDto dto,
            Long currentUserId
    ) {
        ContestEntity contest = findContestOrThrow(contestId);
        ProblemSetEntity problemSet = findProblemSetOrThrow(dto.getProblemSetId());

        checkIsContestOwner(contest, currentUserId);
        checkContestIsDraft(contest);
        checkCanUseProblemSet(problemSet, currentUserId);
        checkProblemSetIsNotEmpty(problemSet.getId());

        ContestProblemSetEntity link = contestProblemSetRepository.findByContestId(contestId)
                .orElseGet(() -> ContestProblemSetEntity.builder()
                        .contest(contest)
                        .build()
                );

        link.setProblemSet(problemSet);

        contestProblemSetRepository.save(link);

        return buildProblemSetShortResponse(problemSet);
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemSetShortResponseDto getContestProblemSet(
            Long contestId,
            Long currentUserId
    ) {
        ContestEntity contest = findContestOrThrow(contestId);

        checkCanViewContest(contest, currentUserId);

        ContestProblemSetEntity link = contestProblemSetRepository.findByContestId(contestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "К соревнованию не привязан набор задач"
                ));

        return buildProblemSetShortResponse(link.getProblemSet());
    }

    @Override
    @Transactional
    public void unbindProblemSetFromContest(
            Long contestId,
            Long currentUserId
    ) {
        ContestEntity contest = findContestOrThrow(contestId);

        checkIsContestOwner(contest, currentUserId);
        checkContestIsDraft(contest);

        ContestProblemSetEntity link = contestProblemSetRepository.findByContestId(contestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "К соревнованию не привязан набор задач"
                ));

        contestProblemSetRepository.delete(link);
    }

    private ContestEntity findContestOrThrow(Long id) {
        return contestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Соревнование не найдено"
                ));
    }

    private ProblemSetEntity findProblemSetOrThrow(Long id) {
        return problemSetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Набор задач не найден"
                ));
    }

    private void checkIsContestOwner(ContestEntity contest, Long currentUserId) {
        if (!Objects.equals(contest.getCreatedByUserId(), currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Нет доступа к изменению этого соревнования"
            );
        }
    }

    private void checkCanViewContest(ContestEntity contest, Long currentUserId) {
        boolean isPublic = Boolean.TRUE.equals(contest.getIsPublic());
        boolean isOwner = Objects.equals(contest.getCreatedByUserId(), currentUserId);

        if (!isPublic && !isOwner) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Нет доступа к соревнованию"
            );
        }
    }

    private void checkContestIsDraft(ContestEntity contest) {
        if (contest.getStatus() != ContestStatusEnum.DRAFT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Набор задач можно изменять только у соревнования в статусе DRAFT"
            );
        }
    }

    private void checkCanUseProblemSet(ProblemSetEntity problemSet, Long currentUserId) {
        boolean isPublic = Boolean.TRUE.equals(problemSet.getIsPublic());
        boolean isOwner = Objects.equals(problemSet.getCreatedByUserId(), currentUserId);

        if (!isPublic && !isOwner) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Нет доступа к использованию этого набора задач"
            );
        }
    }

    private void checkProblemSetIsNotEmpty(Long problemSetId) {
        if (!problemSetTaskRepository.existsByProblemSetId(problemSetId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Нельзя привязать к соревнованию пустой набор задач"
            );
        }
    }

    private ProblemSetShortResponseDto buildProblemSetShortResponse(ProblemSetEntity problemSet) {
        ProblemSetShortResponseDto responseDto = problemSetMapper.toShortResponseDto(problemSet);

        long taskCount = problemSetTaskRepository.countByProblemSetId(problemSet.getId());
        responseDto.setTaskCount(taskCount);

        return responseDto;
    }
}