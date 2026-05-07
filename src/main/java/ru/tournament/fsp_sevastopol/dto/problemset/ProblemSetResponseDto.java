package ru.tournament.fsp_sevastopol.dto.problemset;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSetResponseDto {

    private Long id;

    private String title;

    private String description;

    private Long createdByUserId;

    private Boolean isPublic;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<ProblemSetTaskResponseDto> tasks;
}