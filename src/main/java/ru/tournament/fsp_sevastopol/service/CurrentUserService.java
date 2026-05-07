package ru.tournament.fsp_sevastopol.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tournament.fsp_sevastopol.config.KeycloakJwtAuthenticationConverter;
import ru.tournament.fsp_sevastopol.dto.auth.CurrentUserResponseDto;
import ru.tournament.fsp_sevastopol.entity.UserEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserProvisioningService userProvisioningService;
    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

    @Transactional
    public CurrentUserResponseDto getCurrentUser(Jwt jwt) {
        UserEntity user = userProvisioningService.getOrCreateUser(jwt);

        List<String> roles = keycloakJwtAuthenticationConverter.convert(jwt)
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return CurrentUserResponseDto.builder()
                .userId(user.getId())
                .subjectId(user.getKeycloakSub())
                .username(jwt.getClaimAsString("preferred_username"))
                .email(jwt.getClaimAsString("email"))
                .avatarUrl(user.getAvatarUrl())
                .currentRating(user.getCurrentRating())
                .roles(roles)
                .build();
    }
}
