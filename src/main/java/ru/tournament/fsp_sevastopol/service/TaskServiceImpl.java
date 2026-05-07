package ru.tournament.fsp_sevastopol.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.tournament.fsp_sevastopol.dto.task.TaskRequestDto;
import ru.tournament.fsp_sevastopol.dto.task.TaskResponseDto;
import ru.tournament.fsp_sevastopol.entity.TaskEntity;
import ru.tournament.fsp_sevastopol.mapper.TaskMapper;
import ru.tournament.fsp_sevastopol.repository.ProblemSetTaskRepository;
import ru.tournament.fsp_sevastopol.repository.TaskRepository;
import ru.tournament.fsp_sevastopol.service.interfaces.TaskService;


import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProblemSetTaskRepository problemSetTaskRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public TaskResponseDto createTask(TaskRequestDto dto, Long currentUserId) {
        TaskEntity task = taskMapper.toEntity(dto);
        task.setAuthorUserId(currentUserId);

        TaskEntity savedTask = taskRepository.save(task);

        return taskMapper.toResponseDto(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAvailableTasks(Long currentUserId) {
        return taskRepository.findByIsPublicTrueOrAuthorUserId(currentUserId)
                .stream()
                .map(taskMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id, Long currentUserId) {
        TaskEntity task = findTaskOrThrow(id);

        checkCanViewTask(task, currentUserId);

        return taskMapper.toResponseDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long id, TaskRequestDto dto, Long currentUserId) {
        TaskEntity task = findTaskOrThrow(id);

        checkIsTaskAuthor(task, currentUserId);

        taskMapper.updateEntityFromDto(dto, task);

        TaskEntity savedTask = taskRepository.save(task);

        return taskMapper.toResponseDto(savedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long id, Long currentUserId) {
        TaskEntity task = findTaskOrThrow(id);

        checkIsTaskAuthor(task, currentUserId);

        if (problemSetTaskRepository.existsByTaskId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Задачу нельзя удалить, потому что она используется в наборе задач"
            );
        }

        taskRepository.delete(task);
    }

    private TaskEntity findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Задача не найдена"
                ));
    }

    private void checkCanViewTask(TaskEntity task, Long currentUserId) {
        boolean isPublic = Boolean.TRUE.equals(task.getIsPublic());
        boolean isAuthor = Objects.equals(task.getAuthorUserId(), currentUserId);

        if (!isPublic && !isAuthor) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Нет доступа к задаче"
            );
        }
    }

    private void checkIsTaskAuthor(TaskEntity task, Long currentUserId) {
        if (!Objects.equals(task.getAuthorUserId(), currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Нет доступа к изменению этой задачи"
            );
        }
    }
}