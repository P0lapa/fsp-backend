package ru.tournament.fsp_sevastopol.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.tournament.fsp_sevastopol.dto.contest.ContestFullResponseDto;
import ru.tournament.fsp_sevastopol.dto.contest.ContestRequestDto;
import ru.tournament.fsp_sevastopol.dto.contest.ContestShortResponseDto;
import ru.tournament.fsp_sevastopol.service.ContestService;
import ru.tournament.fsp_sevastopol.service.UserProvisioningService;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Contests", description = "API для управления соревнованиями")
public class ContestController {

    private final ContestService contestService;
    private final UserProvisioningService userProvisioningService;

    @Operation(
            summary = "Создать соревнование",
            description = "Создаёт новое соревнование по спортивному программированию"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Соревнование успешно создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContestFullResponseDto createContest(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ContestRequestDto dto
    ) {
        Long currentUserId = userProvisioningService.getOrCreateUser(jwt).getId();
        return contestService.createContest(dto, currentUserId);
    }

    @Operation(
            summary = "Получить список соревнований",
            description = "Возвращает краткую информацию по всем соревнованиям"
    )
    @ApiResponse(responseCode = "200", description = "Список соревнований успешно получен")
    @GetMapping
    public List<ContestShortResponseDto> getAllContests() {
        return contestService.getAllContests();
    }

    @Operation(
            summary = "Получить соревнование по ID",
            description = "Возвращает полную информацию о конкретном соревновании"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Соревнование найдено"),
            @ApiResponse(responseCode = "404", description = "Соревнование не найдено")
    })
    @GetMapping("/{id}")
    public ContestFullResponseDto getContestById(
            @Parameter(description = "ID соревнования", example = "1")
            @PathVariable Long id
    ) {
        return contestService.getContestById(id);
    }

    @Operation(
            summary = "Изменить соревнование",
            description = "Обновляет данные существующего соревнования"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Соревнование успешно обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "404", description = "Соревнование не найдено")
    })
    @PutMapping("/{id}")
    public ContestFullResponseDto updateContest(
            @Parameter(description = "ID соревнования", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ContestRequestDto dto
    ) {
        return contestService.updateContest(id, dto);
    }

    @Operation(
            summary = "Удалить соревнование",
            description = "Удаляет соревнование по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Соревнование успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Соревнование не найдено")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteContest(
            @Parameter(description = "ID соревнования", example = "1")
            @PathVariable Long id
    ) {
        contestService.deleteContest(id);
    }
}
