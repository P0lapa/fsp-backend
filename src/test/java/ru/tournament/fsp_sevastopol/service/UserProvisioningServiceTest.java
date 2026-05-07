package ru.tournament.fsp_sevastopol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import ru.tournament.fsp_sevastopol.entity.UserEntity;
import ru.tournament.fsp_sevastopol.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProvisioningServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCreationService userCreationService;

    @InjectMocks
    private UserProvisioningService userProvisioningService;

    @Test
    void getOrCreateUserCreatesLocalUserWhenAbsent() {
        Jwt jwt = jwtWithSubject("user-123");
        when(userRepository.findByKeycloakSub("user-123")).thenReturn(Optional.empty());

        UserEntity createdUser = new UserEntity();
        createdUser.setId(7L);
        createdUser.setKeycloakSub("user-123");
        createdUser.setAvatarUrl(UserEntity.DEFAULT_AVATAR_URL);
        createdUser.setCurrentRating(UserEntity.DEFAULT_RATING);
        when(userCreationService.createUser("user-123")).thenReturn(createdUser);

        UserEntity user = userProvisioningService.getOrCreateUser(jwt);

        verify(userRepository).findByKeycloakSub("user-123");
        verify(userCreationService).createUser("user-123");
        assertThat(user.getKeycloakSub()).isEqualTo("user-123");
        assertThat(user.getAvatarUrl()).isEqualTo(UserEntity.DEFAULT_AVATAR_URL);
        assertThat(user.getCurrentRating()).isEqualTo(UserEntity.DEFAULT_RATING);
        assertThat(user).isSameAs(createdUser);
    }

    @Test
    void getOrCreateUserReturnsExistingUserWithoutSaving() {
        Jwt jwt = jwtWithSubject("user-123");
        UserEntity existingUser = new UserEntity();
        existingUser.setId(42L);
        existingUser.setKeycloakSub("user-123");
        existingUser.setAvatarUrl("/images/avatars/custom.png");
        existingUser.setCurrentRating(120);

        when(userRepository.findByKeycloakSub("user-123")).thenReturn(Optional.of(existingUser));

        UserEntity user = userProvisioningService.getOrCreateUser(jwt);

        assertThat(user).isSameAs(existingUser);
        verify(userRepository).findByKeycloakSub("user-123");
        verify(userCreationService, never()).createUser("user-123");
    }

    @Test
    void getOrCreateUserRecoversWhenConcurrentInsertAlreadyCreatedUser() {
        Jwt jwt = jwtWithSubject("user-123");
        UserEntity existingUser = new UserEntity();
        existingUser.setId(42L);
        existingUser.setKeycloakSub("user-123");
        existingUser.setAvatarUrl(UserEntity.DEFAULT_AVATAR_URL);
        existingUser.setCurrentRating(UserEntity.DEFAULT_RATING);

        when(userRepository.findByKeycloakSub("user-123")).thenReturn(Optional.empty(), Optional.of(existingUser));
        when(userCreationService.createUser("user-123"))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        UserEntity user = userProvisioningService.getOrCreateUser(jwt);

        assertThat(user).isSameAs(existingUser);
        verify(userCreationService).createUser("user-123");
        verify(userRepository, times(2)).findByKeycloakSub("user-123");
    }

    private static Jwt jwtWithSubject(String subject) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(subject)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
