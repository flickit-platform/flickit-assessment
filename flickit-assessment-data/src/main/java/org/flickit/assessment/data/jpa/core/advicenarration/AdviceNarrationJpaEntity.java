package org.flickit.assessment.data.jpa.core.advicenarration;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "faa_advice_narration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AdviceNarrationJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "assessment_result_id", updatable = false, nullable = false)
    private UUID assessmentResultId;

    @Column(name = "ai_narration", length = 1000, nullable = false)
    private String aiNarration;

    @Column(name = "assessor_narration", length = 1000)
    private String assessorNarration;

    @Column(name = "ai_narration_time", nullable = false)
    private LocalDateTime aiNarrationTime;

    @Column(name = "assessor_narration_time")
    private LocalDateTime assessorNarrationTime;

    @Column(name = "created_by")
    private UUID createdBy;

}
