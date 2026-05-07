package ru.tournament.fsp_sevastopol.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequestDto {

    @NotBlank(message = "Название задачи обязательно")
    private String title;

    @NotBlank(message = "Краткое название задачи обязательно")
    private String shortName;

    @NotBlank(message = "Условие задачи обязательно")
    private String statement;

    private String inputDescription;

    private String outputDescription;

    private String notes;

    private String exampleInput;

    private String exampleOutput;

    private String constraintsText;

    @NotNull(message = "Ограничение по времени обязательно")
    @Positive(message = "Ограничение по времени должно быть положительным")
    private Integer timeLimitMs;

    @NotNull(message = "Ограничение по памяти обязательно")
    @Positive(message = "Ограничение по памяти должно быть положительным")
    private Integer memoryLimitMb;

    private String source;

    @NotNull(message = "Признак публичности обязателен")
    private Boolean isPublic;
}