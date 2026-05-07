package ru.tournament.fsp_sevastopol.dto.problemset;

import lombok.*;
import ru.tournament.fsp_sevastopol.dto.task.TaskShortResponseDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSetTaskResponseDto {

    private Long id;

    private Long problemSetId;

    private Long taskId;

    private Integer orderNum;

    private String contestLabel;

    private Integer score;

    private TaskShortResponseDto task;
}