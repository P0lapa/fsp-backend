package ru.tournament.fsp_sevastopol.dto.problemset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSetRequestDto {

    @NotBlank(message = "Название набора задач обязательно")
    private String title;

    private String description;

    @NotNull(message = "Признак публичности обязателен")
    private Boolean isPublic;
}