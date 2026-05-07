package ru.tournament.fsp_sevastopol.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.tournament.fsp_sevastopol.entity.UserEntity;
import ru.tournament.fsp_sevastopol.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserCreationService {

    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserEntity createUser(String keycloakSub) {
        UserEntity user = new UserEntity();
        user.setKeycloakSub(keycloakSub);
        user.setAvatarUrl(UserEntity.DEFAULT_AVATAR_URL);
        user.setCurrentRating(UserEntity.DEFAULT_RATING);
        return userRepository.save(user);
    }
}
