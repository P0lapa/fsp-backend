package ru.tournament.fsp_sevastopol.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.tournament.fsp_sevastopol.dto.problemset.*;
import ru.tournament.fsp_sevastopol.entity.ContestProblemSetEntity;
import ru.tournament.fsp_sevastopol.entity.ProblemSetEntity;
import ru.tournament.fsp_sevastopol.entity.ProblemSetTaskEntity;
import ru.tournament.fsp_sevastopol.entity.TaskEntity;
import ru.tournament.fsp_sevastopol.enums.ContestStatusEnum;
import ru.tournament.fsp_sevastopol.mapper.ProblemSetMapper;
import ru.tournament.fsp_sevastopol.mapper.ProblemSetTaskMapper;
import ru.tournament.fsp_sevastopol.repository.ContestProblemSetRepository;
import ru.tournament.fsp_sevastopol.repository.ProblemSetRepository;
import ru.tournament.fsp_sevastopol.repository.ProblemSetTaskRepository;
import ru.tournament.fsp_sevastopol.repository.TaskRepository;
import ru.tournament.fsp_sevastopol.service.interfaces.ProblemSetService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProblemSetServiceImpl implements ProblemSetService {

    private final ProblemSetRepository problemSetRepository;
    private final ProblemSetTaskRepository problemSetTaskRepository;
    private final ContestProblemSetRepository contestProblemSetRepository;
    private final TaskRepository taskRepository;

    private final ProblemSetMapper problemSetMapper;
    private final ProblemSetTaskMapper problemSetTaskMapper;

    @Override
    @Transactional
    public ProblemSetResponseDto createProblemSet(ProblemSetRequestDto dto, Long currentUserId) {
        ProblemSetEntity problemSet = problemSetMapper.toEntity(dto);
        problemSet.setCreatedByUserId(currentUserId);

        ProblemSetEntity savedProblemSet = problemSetRepository.save(problemSet);

        return buildProblemSetResponse(savedProblemSet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemSetShortResponseDto> getAvailableProblemSets(Long currentUserId) {
        return problemSetRepository.findByIsPublicTrueOrCreatedByUserId(currentUserId)
                .stream()
                .map(this::buildProblemSetShortResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemSetResponseDto getProblemSetById(Long id, Long currentUserId) {
        ProblemSetEntity problemSet = findProblemSetOrThrow(id);

        checkCanViewProblemSet(problemSet, currentUserId);

        return buildProblemSetResponse(problemSet);
    }

    @Override
    @Transactional
    public ProblemSetResponseDto updateProblemSet(Long id, ProblemSetRequestDto dto, Long currentUserId) {
        ProblemSetEntity problemSet = findProblemSetOrThrow(id);

        checkIsProblemSetOwner(problemSet, currentUserId);
        checkProblemSetIsEditable(problemSet.getId());

        problemSetMapper.updateEntityFromDto(dto, problemSet);

        ProblemSetEntity savedProblemSet = problemSetRepository.save(problemSet);

        return buildProblemSetResponse(savedProblemSet);
    }

    @Override
    @Transactional
    public void deleteProblemSet(Long id, Long currentUserId) {
        ProblemSetEntity problemSet = findProblemSetOrThrow(id);

        checkIsProblemSetOwner(problemSet, currentUserId);
        checkProblemSetIsEditable(problemSet.getId());

        problemSetTaskRepository.deleteByProblemSetId(id);
        problemSetRepository.delete(problemSet);
    }

    @Override
    @Transactional
    public ProblemSetTaskResponseDto addTaskToProblemSet(
            Long problemSetId,
            ProblemSetTaskRequestDto dto,
            Long currentUserId
    ) {
        ProblemSetEntity problemSet = findProblemSetOrThrow(problemSetId);

        checkIsProblemSetOwner(problemSet, currentUserId);
        checkProblemSetIsEditable(problemSetId);

        TaskEntity task = findTaskOrThrow(dto.getTaskId());
        checkCanUseTask(task, currentUserId);

        checkTaskNotAlreadyInProblemSet(problemSetId, dto.getTaskId());
        checkOrderNumNotAlreadyUsed(problemSetId, dto.getOrderNum());
        checkContestLabelNotAlreadyUsed(problemSetId, dto.getContestLabel());

        ProblemSetTaskEntity link = problemSetTaskMapper.toEntity(dto);
        link.setProblemSet(problemSet);
        link.setTask(task);

        ProblemSetTaskEntity savedLink = problemSetTaskRepository.save(link);

        return problemSetTaskMapper.toResponseDto(savedLink);
    }

    @Override
    @Transactional
    public ProblemSetTaskResponseDto updateProblemSetTask(
            Long problemSetId,
            Long linkId,
            ProblemSetTaskRequestDto dto,
            Long currentUserId
    ) {
        ProblemSetEntity problemSet = findProblemSetOrThrow(problemSetId);

        checkIsProblemSetOwner(problemSet, currentUserId);
        checkProblemSetIsEditable(problemSetId);

        ProblemSetTaskEntity link = findProblemSetTaskOrThrow(problemSetId, linkId);

        TaskEntity task = findTaskOrThrow(dto.getTaskId());
        checkCanUseTask(task, currentUserId);

        checkTaskNotAlreadyInProblemSetForUpdate(problemSetId, dto.getTaskId(), linkId);
        checkOrderNumNotAlreadyUsedForUpdate(problemSetId, dto.getOrderNum(), linkId);
        checkContestLabelNotAlreadyUsedForUpdate(problemSetId, dto.getContestLabel(), linkId);

        problemSetTaskMapper.updateEntityFromDto(dto, link);
        link.setTask(task);

        ProblemSetTaskEntity savedLink = problemSetTaskRepository.save(link);

        return problemSetTaskMapper.toResponseDto(savedLink);
    }

    @Override
    @Transactional
    public void removeTaskFromProblemSet(
            Long problemSetId,
            Long linkId,
            Long currentUserId
    ) {
        ProblemSetEntity problemSet = findProblemSetOrThrow(problemSetId);

        checkIsProblemSetOwner(problemSet, currentUserId);
        checkProblemSetIsEditable(problemSetId);

        ProblemSetTaskEntity link = findProblemSetTaskOrThrow(problemSetId, linkId);

        problemSetTaskRepository.delete(link);
    }

    private ProblemSetEntity findProblemSetOrThrow(Long id) {
        return problemSetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Набор задач не найден"
                ));
    }

    private TaskEntity findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Задача не найдена"
                ));
    }

    private ProblemSetTaskEntity findProblemSetTaskOrThrow(Long problemSetId, Long linkId) {
        return problemSetTaskRepository.findByIdAndProblemSetId(linkId, problemSetId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Задача не найдена в составе этого набора"
                ));
    }

    private void checkCanViewProblemSet(ProblemSetEntity problemSet, Long currentUserId) {
        boolean isPublic = Boolean.TRUE.equals(problemSet.getIsPublic());
        boolean isOwner = Objects.equals(problemSet.getCreatedByUserId(), currentUserId);

        if (!isPublic && !isOwner) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Нет доступа к набору задач"
            );
        }
    }

    private void checkIsProblemSetOwner(ProblemSetEntity problemSet, Long currentUserId) {
        if (!Objects.equals(problemSet.getCreatedByUserId(), currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Нет доступа к изменению этого набора задач"
            );
        }
    }

    private void checkCanUseTask(TaskEntity task, Long currentUserId) {
        boolean isPublic = Boolean.TRUE.equals(task.getIsPublic());
        boolean isAuthor = Objects.equals(task.getAuthorUserId(), currentUserId);

        if (!isPublic && !isAuthor) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Нет доступа к использованию этой задачи"
            );
        }
    }

    private void checkTaskNotAlreadyInProblemSet(Long problemSetId, Long taskId) {
        if (problemSetTaskRepository.existsByProblemSetIdAndTaskId(problemSetId, taskId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Эта задача уже добавлена в набор"
            );
        }
    }

    private void checkOrderNumNotAlreadyUsed(Long problemSetId, Integer orderNum) {
        if (problemSetTaskRepository.existsByProblemSetIdAndOrderNum(problemSetId, orderNum)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Такой порядковый номер уже используется в этом наборе задач"
            );
        }
    }

    private void checkContestLabelNotAlreadyUsed(Long problemSetId, String contestLabel) {
        if (problemSetTaskRepository.existsByProblemSetIdAndContestLabel(problemSetId, contestLabel)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Такое буквенное обозначение уже используется в этом наборе задач"
            );
        }
    }

    private void checkTaskNotAlreadyInProblemSetForUpdate(Long problemSetId, Long taskId, Long linkId) {
        if (problemSetTaskRepository.existsByProblemSetIdAndTaskIdAndIdNot(problemSetId, taskId, linkId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Эта задача уже добавлена в набор"
            );
        }
    }

    private void checkOrderNumNotAlreadyUsedForUpdate(Long problemSetId, Integer orderNum, Long linkId) {
        if (problemSetTaskRepository.existsByProblemSetIdAndOrderNumAndIdNot(problemSetId, orderNum, linkId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Такой порядковый номер уже используется в этом наборе задач"
            );
        }
    }

    private void checkContestLabelNotAlreadyUsedForUpdate(Long problemSetId, String contestLabel, Long linkId) {
        if (problemSetTaskRepository.existsByProblemSetIdAndContestLabelAndIdNot(problemSetId, contestLabel, linkId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Такое буквенное обозначение уже используется в этом наборе задач"
            );
        }
    }

    private void checkProblemSetIsEditable(Long problemSetId) {
        List<ContestProblemSetEntity> links = contestProblemSetRepository.findByProblemSetId(problemSetId);

        boolean usedByNonDraftContest = links.stream()
                .anyMatch(link -> link.getContest().getStatus() != ContestStatusEnum.DRAFT);

        if (usedByNonDraftContest) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Набор задач нельзя изменить, потому что он используется в соревновании не в статусе DRAFT"
            );
        }
    }

    private ProblemSetResponseDto buildProblemSetResponse(ProblemSetEntity problemSet) {
        List<ProblemSetTaskResponseDto> tasks = problemSetTaskRepository
                .findByProblemSetIdOrderByOrderNumAsc(problemSet.getId())
                .stream()
                .map(problemSetTaskMapper::toResponseDto)
                .toList();

        ProblemSetResponseDto responseDto = problemSetMapper.toResponseDto(problemSet);
        responseDto.setTasks(tasks);

        return responseDto;
    }

    private ProblemSetShortResponseDto buildProblemSetShortResponse(ProblemSetEntity problemSet) {
        ProblemSetShortResponseDto responseDto = problemSetMapper.toShortResponseDto(problemSet);

        long taskCount = problemSetTaskRepository.countByProblemSetId(problemSet.getId());
        responseDto.setTaskCount(taskCount);

        return responseDto;
    }
}