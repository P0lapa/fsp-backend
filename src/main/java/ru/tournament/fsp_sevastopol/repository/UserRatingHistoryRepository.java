package ru.tournament.fsp_sevastopol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tournament.fsp_sevastopol.entity.UserRatingHistoryEntity;

public interface UserRatingHistoryRepository extends JpaRepository<UserRatingHistoryEntity, Long> {
}
