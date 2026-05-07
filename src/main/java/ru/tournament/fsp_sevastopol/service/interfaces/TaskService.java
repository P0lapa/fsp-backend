package ru.tournament.fsp_sevastopol.service.interfaces;

import org.springframework.security.oauth2.jwt.Jwt;
import ru.tournament.fsp_sevastopol.dto.task.TaskRequestDto;
import ru.tournament.fsp_sevastopol.dto.task.TaskResponseDto;

import java.util.List;

public interface TaskService {

    TaskResponseDto createTask(TaskRequestDto dto, Long currentUserId);

    List<TaskResponseDto> getAvailableTasks(Long currentUserId);

    TaskResponseDto getTaskById(Long id, Long currentUserId);

    TaskResponseDto updateTask(Long id, TaskRequestDto dto, Long currentUserId);

    void deleteTask(Long id, Long currentUserId);
}