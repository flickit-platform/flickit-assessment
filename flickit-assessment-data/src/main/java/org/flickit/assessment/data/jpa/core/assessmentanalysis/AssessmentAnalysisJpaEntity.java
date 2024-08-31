package org.flickit.assessment.data.jpa.core.assessmentanalysis;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fac_assessment_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentAnalysisJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "assessment_result_id", nullable = false)
    private UUID assessmentResultId;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "ai_analysis")
    private String aiAnalysis;

    @Column(name = "assessor_analysis")
    private String assessorAnalysis;

    @Column(name = "ai_analysis_time")
    private LocalDateTime aiAnalysisTime;

    @Column(name = "assessor_analysis_time")
    private LocalDateTime assessorAnalysisTime;

    @Column(name = "input_path", nullable = false)
    private String inputPath;
}
