package ru.tournament.fsp_sevastopol.dto.contest;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.tournament.fsp_sevastopol.enums.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ContestRequestDto {

    @NotBlank(message = "Название соревнования обязательно")
    private String title;

    private String description;

    @NotNull(message = "Формат соревнования обязателен")
    private ContestFormatEnum format;

    @NotNull(message = "Тип участия обязателен")
    private ParticipationTypeEnum participationType;

    @NotNull(message = "Уровень соревнования обязателен")
    private ContestLevelEnum level;

    @NotEmpty(message = "Нужно указать хотя бы один поддерживаемый язык программирования")
    private Set<ProgrammingLanguageEnum> supportedLanguages;

    @NotNull(message = "Видимость соревнования обязательна")
    private Boolean isPublic;

    @NotNull(message = "Статус соревнования обязателен")
    private ContestStatusEnum status;

    @FutureOrPresent(message = "Дата начала регистрации не может быть в прошлом")
    private LocalDateTime registrationStartAt;

    @Future(message = "Дата окончания регистрации должна быть в будущем")
    private LocalDateTime registrationEndAt;

    @NotNull(message = "Дата начала соревнования обязательна")
    @Future(message = "Дата начала соревнования должна быть в будущем")
    private LocalDateTime startAt;

    @NotNull(message = "Дата окончания соревнования обязательна")
    @Future(message = "Дата окончания соревнования должна быть в будущем")
    private LocalDateTime endAt;

    @Min(value = 1, message = "Максимальный размер команды должен быть не меньше 1")
    private Integer maxTeamSize;

    private Long problemSetId;
}
