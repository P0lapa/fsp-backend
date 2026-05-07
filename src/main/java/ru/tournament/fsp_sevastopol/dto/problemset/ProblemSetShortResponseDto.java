package ru.tournament.fsp_sevastopol.dto.problemset;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSetShortResponseDto {

    private Long id;

    private String title;

    private String description;

    private Long taskCount;
}