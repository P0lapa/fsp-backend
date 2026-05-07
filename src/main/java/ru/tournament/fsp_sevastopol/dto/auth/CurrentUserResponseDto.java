package ru.tournament.fsp_sevastopol.dto.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CurrentUserResponseDto {

    private Long userId;
    private String subjectId;
    private String username;
    private String email;
    private String avatarUrl;
    private Integer currentRating;
    private List<String> roles;
}
