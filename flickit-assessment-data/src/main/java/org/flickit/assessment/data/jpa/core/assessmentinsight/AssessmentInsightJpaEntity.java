package org.flickit.assessment.data.jpa.core.assessmentinsight;

import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "assessment_result_id", nullable = false)
    private UUID assessmentResultId;

    @Column(name = "insight", nullable = false)
    private String insight;

    @Column(name = "insight_time", nullable = false)
    private LocalDateTime insightTime;

    @Column(name = "insight_by", nullable = false)
    private UUID insightBy;
}
