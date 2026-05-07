package ru.tournament.fsp_sevastopol.dto.problemset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSetTaskRequestDto {

    @NotNull(message = "ID задачи обязателен")
    private Long taskId;

    @NotNull(message = "Порядковый номер задачи обязателен")
    @Positive(message = "Порядковый номер должен быть положительным")
    private Integer orderNum;

    @NotBlank(message = "Буквенное обозначение задачи обязательно")
    private String contestLabel;

    @PositiveOrZero(message = "Баллы не могут быть отрицательными")
    private Integer score;
}