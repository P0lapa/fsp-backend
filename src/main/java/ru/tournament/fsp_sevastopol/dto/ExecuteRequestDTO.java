package ru.tournament.fsp_sevastopol.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExecuteRequestDTO {
    private String submissionId;
    private String userId;
    private String taskId;
    private String code;
    private String lang;
    private List<String> input;
    private Integer timeLimitMs;
    private Integer memoryLimitMb;
    private List<String> additionalArgs;
}