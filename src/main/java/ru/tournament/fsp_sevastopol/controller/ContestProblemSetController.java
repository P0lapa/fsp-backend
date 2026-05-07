package ru.tournament.fsp_sevastopol.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.tournament.fsp_sevastopol.dto.problemset.BindProblemSetToContestRequestDto;
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetShortResponseDto;
import ru.tournament.fsp_sevastopol.service.UserProvisioningService;
import ru.tournament.fsp_sevastopol.service.interfaces.ContestProblemSetService;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
@Tag(name = "Contest Problem Sets", description = "API для привязки наборов задач к соревнованиям")
public class ContestProblemSetController {

    private final ContestProblemSetService contestProblemSetService;
    private final UserProvisioningService userProvisioningService;

    @PutMapping("/{contestId}/problem-set")
    @Operation(
            summary = "Привязать набор задач к соревнованию",
            description = "Создаёт или заменяет связь соревнования с набором задач. Привязываемый набор должен содержать минимум одну задачу.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Набор задач привязан к соревнованию"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос или пустой набор задач"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к изменению соревнования или набору задач"),
            @ApiResponse(responseCode = "404", description = "Соревнование или набор задач не найдены"),
            @ApiResponse(responseCode = "409", description = "Набор задач нельзя изменить из-за статуса соревнования")
    })
    public ProblemSetShortResponseDto bindProblemSetToContest(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long contestId,
            @Valid @RequestBody BindProblemSetToContestRequestDto dto
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return contestProblemSetService.bindProblemSetToContest(contestId, dto, currentUserId);
    }

    @GetMapping("/{contestId}/problem-set")
    @Operation(
            summary = "Получить набор задач соревнования",
            description = "Возвращает краткую информацию о наборе задач, привязанном к соревнованию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Набор задач соревнования получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к соревнованию"),
            @ApiResponse(responseCode = "404", description = "Соревнование или привязанный набор задач не найдены")
    })
    public ProblemSetShortResponseDto getContestProblemSet(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long contestId
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return contestProblemSetService.getContestProblemSet(contestId, currentUserId);
    }

    @DeleteMapping("/{contestId}/problem-set")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Отвязать набор задач от соревнования",
            description = "Удаляет связь соревнования с набором задач. Разрешено только для соревнования в статусе DRAFT.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Набор задач отвязан от соревнования"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к изменению соревнования"),
            @ApiResponse(responseCode = "404", description = "Соревнование или связь с набором задач не найдены"),
            @ApiResponse(responseCode = "409", description = "Набор задач нельзя отвязать из-за статуса соревнования")
    })
    public void unbindProblemSetFromContest(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long contestId
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        contestProblemSetService.unbindProblemSetFromContest(contestId, currentUserId);
    }
}
