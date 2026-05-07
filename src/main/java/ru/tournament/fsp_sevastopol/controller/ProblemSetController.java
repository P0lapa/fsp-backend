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
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetRequestDto;
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetResponseDto;
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetShortResponseDto;
import ru.tournament.fsp_sevastopol.service.UserProvisioningService;
import ru.tournament.fsp_sevastopol.service.interfaces.ProblemSetService;

import java.util.List;

@RestController
@RequestMapping("/api/problem-sets")
@RequiredArgsConstructor
@Tag(name = "Problem Sets", description = "API для управления наборами задач")
public class ProblemSetController {

    private final ProblemSetService problemSetService;
    private final UserProvisioningService userProvisioningService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать набор задач",
            description = "Создаёт самостоятельный набор задач. Набор может быть пустым на этапе подготовки.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Набор задач успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные набора задач"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public ProblemSetResponseDto createProblemSet(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ProblemSetRequestDto dto
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return problemSetService.createProblemSet(dto, currentUserId);
    }

    @GetMapping
    @Operation(
            summary = "Получить доступные наборы задач",
            description = "Возвращает публичные наборы задач и наборы, созданные текущим пользователем.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список наборов задач получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public List<ProblemSetShortResponseDto> getAvailableProblemSets(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return problemSetService.getAvailableProblemSets(currentUserId);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить набор задач по ID",
            description = "Возвращает метаданные набора задач и его состав.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Набор задач найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к набору задач"),
            @ApiResponse(responseCode = "404", description = "Набор задач не найден")
    })
    public ProblemSetResponseDto getProblemSetById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return problemSetService.getProblemSetById(id, currentUserId);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить набор задач",
            description = "Обновляет название, описание и публичность набора задач.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Набор задач успешно обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные набора задач"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к изменению набора задач"),
            @ApiResponse(responseCode = "404", description = "Набор задач не найден"),
            @ApiResponse(responseCode = "409", description = "Набор нельзя изменить из-за связанного соревнования")
    })
    public ProblemSetResponseDto updateProblemSet(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody ProblemSetRequestDto dto
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return problemSetService.updateProblemSet(id, dto, currentUserId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить набор задач",
            description = "Удаляет набор задач, если он принадлежит текущему пользователю и не связан с соревнованием, где удаление запрещено.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Набор задач успешно удалён"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к удалению набора задач"),
            @ApiResponse(responseCode = "404", description = "Набор задач не найден"),
            @ApiResponse(responseCode = "409", description = "Набор нельзя удалить из-за связанных данных")
    })
    public void deleteProblemSet(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        problemSetService.deleteProblemSet(id, currentUserId);
    }
}