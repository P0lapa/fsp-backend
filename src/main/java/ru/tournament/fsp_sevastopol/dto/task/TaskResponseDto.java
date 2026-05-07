package ru.tournament.fsp_sevastopol.dto.task;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDto {

    private Long id;

    private String title;

    private String shortName;

    private String statement;

    private String inputDescription;

    private String outputDescription;

    private String notes;

    private String exampleInput;

    private String exampleOutput;

    private String constraintsText;

    private Integer timeLimitMs;

    private Integer memoryLimitMb;

    private String source;

    private Long authorUserId;

    private Boolean isPublic;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}