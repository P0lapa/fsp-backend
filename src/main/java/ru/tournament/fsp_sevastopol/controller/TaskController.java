package ru.tournament.fsp_sevastopol.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.tournament.fsp_sevastopol.dto.task.TaskRequestDto;
import ru.tournament.fsp_sevastopol.dto.task.TaskResponseDto;
import ru.tournament.fsp_sevastopol.service.UserProvisioningService;
import ru.tournament.fsp_sevastopol.service.interfaces.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "API для управления задачами спортивного программирования")
public class TaskController {

    private final TaskService taskService;
    private final UserProvisioningService userProvisioningService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать задачу",
            description = "Создаёт самостоятельную задачу. Автор задачи определяется на сервере по текущему пользователю.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные задачи"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public TaskResponseDto createTask(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TaskRequestDto dto
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return taskService.createTask(dto, currentUserId);
    }

    @GetMapping
    @Operation(
            summary = "Получить доступные задачи",
            description = "Возвращает публичные задачи и задачи, созданные текущим пользователем.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список задач получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public List<TaskResponseDto> getAvailableTasks(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return taskService.getAvailableTasks(currentUserId);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить задачу по ID",
            description = "Возвращает полное описание задачи, если задача публичная или принадлежит текущему пользователю.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к задаче"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public TaskResponseDto getTaskById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return taskService.getTaskById(id, currentUserId);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить задачу",
            description = "Обновляет задачу. authorUserId, createdAt и updatedAt не принимаются от клиента.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные задачи"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к изменению задачи"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public TaskResponseDto updateTask(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDto dto
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return taskService.updateTask(id, dto, currentUserId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить задачу",
            description = "Удаляет задачу, если она принадлежит текущему пользователю и не используется в наборе задач.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к удалению задачи"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "409", description = "Задачу нельзя удалить из-за связанных данных")
    })
    public void deleteTask(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        taskService.deleteTask(id, currentUserId);
    }
}