package ru.tournament.fsp_sevastopol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tournament.fsp_sevastopol.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByKeycloakSub(String keycloakSub);
}
