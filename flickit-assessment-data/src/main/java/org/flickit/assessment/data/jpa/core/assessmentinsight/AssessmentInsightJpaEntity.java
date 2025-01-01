package org.flickit.assessment.data.jpa.core.assessmentinsight;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fac_assessment_insight")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentInsightJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "assessment_result_id", nullable = false)
    private UUID assessmentResultId;

    @Column(name = "insight", nullable = false)
    private String insight;

    @Column(name = "insight_time", nullable = false)
    private LocalDateTime insightTime;

    @Column(name = "insight_by")
    private UUID insightBy;

    @Column(name = "approved", nullable = false)
    private Boolean approved;
}
