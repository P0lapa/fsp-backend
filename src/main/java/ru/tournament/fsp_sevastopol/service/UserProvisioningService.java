package ru.tournament.fsp_sevastopol.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.tournament.fsp_sevastopol.entity.UserEntity;
import ru.tournament.fsp_sevastopol.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserProvisioningService {

    private final UserRepository userRepository;
    private final UserCreationService userCreationService;

    public UserEntity getOrCreateUser(Jwt jwt) {
        String subject = jwt.getSubject();

        return userRepository.findByKeycloakSub(subject)
                .orElseGet(() -> createUserOrRecover(subject));
    }

    private UserEntity createUserOrRecover(String keycloakSub) {
        try {
            return userCreationService.createUser(keycloakSub);
        } catch (DataIntegrityViolationException ex) {
            return userRepository.findByKeycloakSub(keycloakSub)
                    .orElseThrow(() -> ex);
        }
    }
}
