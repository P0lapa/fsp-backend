package ru.tournament.fsp_sevastopol.controller;

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
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetTaskRequestDto;
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetTaskResponseDto;
import ru.tournament.fsp_sevastopol.service.UserProvisioningService;
import ru.tournament.fsp_sevastopol.service.interfaces.ProblemSetService;

@RestController
@RequestMapping("/api/problem-sets/{problemSetId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Problem Set Tasks", description = "API для управления составом набора задач")
public class ProblemSetTaskController {

    private final ProblemSetService problemSetService;
    private final UserProvisioningService userProvisioningService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Добавить задачу в набор",
            description = "Добавляет существующую задачу в набор задач с порядковым номером, буквенным обозначением и баллами.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Задача добавлена в набор"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные связи задачи и набора"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к изменению набора задач"),
            @ApiResponse(responseCode = "404", description = "Набор задач или задача не найдены"),
            @ApiResponse(responseCode = "409", description = "Задача, порядок или буквенное обозначение уже используются в этом наборе")
    })
    public ProblemSetTaskResponseDto addTaskToProblemSet(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long problemSetId,
            @Valid @RequestBody ProblemSetTaskRequestDto dto
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return problemSetService.addTaskToProblemSet(problemSetId, dto, currentUserId);
    }

    @PutMapping("/{linkId}")
    @Operation(
            summary = "Обновить задачу в составе набора",
            description = "Обновляет связь задачи с набором: задачу, порядок, буквенное обозначение или баллы.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Связь задачи и набора обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные связи задачи и набора"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к изменению набора задач"),
            @ApiResponse(responseCode = "404", description = "Набор задач, задача или связь не найдены"),
            @ApiResponse(responseCode = "409", description = "Задача, порядок или буквенное обозначение уже используются в этом наборе")
    })
    public ProblemSetTaskResponseDto updateProblemSetTask(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long problemSetId,
            @PathVariable Long linkId,
            @Valid @RequestBody ProblemSetTaskRequestDto dto
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return problemSetService.updateProblemSetTask(problemSetId, linkId, dto, currentUserId);
    }

    @DeleteMapping("/{linkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить задачу из набора",
            description = "Удаляет задачу из состава набора по ID связи. Сама задача при этом не удаляется.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Задача удалена из набора"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к изменению набора задач"),
            @ApiResponse(responseCode = "404", description = "Набор задач или связь не найдены"),
            @ApiResponse(responseCode = "409", description = "Состав набора нельзя изменить из-за связанного соревнования")
    })
    public void removeTaskFromProblemSet(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long problemSetId,
            @PathVariable Long linkId
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        problemSetService.removeTaskFromProblemSet(problemSetId, linkId, currentUserId);
    }
}