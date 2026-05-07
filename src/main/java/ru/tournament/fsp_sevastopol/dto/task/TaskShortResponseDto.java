package ru.tournament.fsp_sevastopol.dto.task;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskShortResponseDto {

    private Long id;

    private String title;

    private String shortName;

    private Integer timeLimitMs;

    private Integer memoryLimitMb;
}