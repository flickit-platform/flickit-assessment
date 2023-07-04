package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fac_assessment_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentResultJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assessment_id", referencedColumnName = "id", nullable = false)
    private AssessmentJpaEntity assessment;

    @Column(name = "maturity_level_id")
    private Long maturityLevelId;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationDate;

    @Column(name = "is_valid")
    private Boolean isValid;

    @Override
    public String toString() {
        return id.toString();
    }

}
