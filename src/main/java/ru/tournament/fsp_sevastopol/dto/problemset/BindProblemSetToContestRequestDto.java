package ru.tournament.fsp_sevastopol.dto.problemset;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BindProblemSetToContestRequestDto {

    @NotNull(message = "ID набора задач обязателен")
    private Long problemSetId;
}