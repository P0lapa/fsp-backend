package ru.tournament.fsp_sevastopol.dto.contest;

import lombok.Data;
import ru.tournament.fsp_sevastopol.enums.ContestLevelEnum;
import ru.tournament.fsp_sevastopol.enums.ProgrammingLanguageEnum;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ContestShortResponseDto {

    private Long id;
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime registrationEndAt;
    private ContestLevelEnum level;
    private Set<ProgrammingLanguageEnum> supportedLanguages;
}