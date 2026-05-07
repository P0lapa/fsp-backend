package ru.tournament.fsp_sevastopol.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "problem_set_tasks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_problem_set_order_num",
                        columnNames = {"problem_set_id", "order_num"}
                ),
                @UniqueConstraint(
                        name = "uk_problem_set_contest_label",
                        columnNames = {"problem_set_id", "contest_label"}
                ),
                @UniqueConstraint(
                        name = "uk_problem_set_task",
                        columnNames = {"problem_set_id", "task_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSetTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_set_id", nullable = false)
    private ProblemSetEntity problemSet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    @Column(name = "order_num", nullable = false)
    private Integer orderNum;

    @Column(name = "contest_label", nullable = false, length = 10)
    private String contestLabel;

    @Column
    private Integer score;
}
