package org.flickit.assessment.data.jpa.users.usersurvey;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fau_user_survey")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserSurveyJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fau_user_survey_id_seq")
    @SequenceGenerator(name = "fau_user_survey_id_seq", sequenceName = "fau_user_survey_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "assessment_id", updatable = false, nullable = false)
    private UUID assessmentId;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @Column(name = "dont_show_again", nullable = false)
    private boolean dontShowAgain;

    @Column(name = "completion_time")
    private LocalDateTime completionTime;

    @Column(name = "creation_time", updatable = false, nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;
}
