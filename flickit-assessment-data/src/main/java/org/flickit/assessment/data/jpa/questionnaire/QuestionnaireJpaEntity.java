package org.flickit.assessment.data.jpa.questionnaire;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.assessmentkit.AssessmentKitJpaEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "baseinfo_questionnaire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class QuestionnaireJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime lastModificationDate;

    @Column(name = "index", nullable = false)
    private Integer index;

    @ManyToOne
    @JoinColumn(name = "assessment_kit_id", referencedColumnName = "id")
    private AssessmentKitJpaEntity assessmentKit;
}
