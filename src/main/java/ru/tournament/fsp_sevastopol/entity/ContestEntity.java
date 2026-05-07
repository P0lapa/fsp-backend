package ru.tournament.fsp_sevastopol.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.tournament.fsp_sevastopol.enums.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "contests")
public class ContestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContestFormatEnum format;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationTypeEnum participationType;

    @Column(nullable = false)
    private Boolean isPublic = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContestStatusEnum status = ContestStatusEnum.DRAFT;

    private LocalDateTime registrationStartAt;
    private LocalDateTime registrationEndAt;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    private Integer maxTeamSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContestLevelEnum level;

    @ElementCollection
    @CollectionTable(
            name = "contest_supported_languages",
            joinColumns = @JoinColumn(name = "contest_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Set<ProgrammingLanguageEnum> supportedLanguages;

    @Column(nullable = false)
    private Long createdByUserId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}