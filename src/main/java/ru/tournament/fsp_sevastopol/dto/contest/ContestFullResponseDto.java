package ru.tournament.fsp_sevastopol.dto.contest;

import lombok.Data;
import ru.tournament.fsp_sevastopol.dto.problemset.ProblemSetShortResponseDto;
import ru.tournament.fsp_sevastopol.enums.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ContestFullResponseDto {

    private Long id;
    private String title;
    private String description;
    private ContestFormatEnum format;
    private ParticipationTypeEnum participationType;
    private ContestLevelEnum level;
    private Set<ProgrammingLanguageEnum> supportedLanguages;
    private Boolean isPublic;
    private ContestStatusEnum status;
    private LocalDateTime registrationStartAt;
    private LocalDateTime registrationEndAt;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer maxTeamSize;
    private Long createdByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProblemSetShortResponseDto problemSet;
}