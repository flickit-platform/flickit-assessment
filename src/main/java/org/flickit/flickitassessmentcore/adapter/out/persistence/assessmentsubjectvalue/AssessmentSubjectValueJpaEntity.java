package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentsubjectvalue;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "assessment_assessmentsubjectvalue")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AssessmentSubjectValueJpaEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assessment_result_id", referencedColumnName = "id", nullable = false)
    private AssessmentResultJpaEntity assessmentResult;
    @Column(name = "assessment_subject_id", nullable = false)
    private Long assessmentSubjectId;
    @Column(name = "maturity_level_id")
    private Long maturityLevelId;

    @Override
    public String toString() {
        return id.toString();
    }

}
