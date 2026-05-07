package ru.tournament.fsp_sevastopol.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "contest_problem_sets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_contest_problem_set_contest",
                        columnNames = {"contest_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestProblemSetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_id", nullable = false)
    private ContestEntity contest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_set_id", nullable = false)
    private ProblemSetEntity problemSet;
}