package org.flickit.assessment.core.adapter.out.persistence.assessmentresult;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.answer.AnswerJpaEntity;
import org.flickit.assessment.core.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.assessment.core.adapter.out.persistence.assessmentsubjectvalue.AssessmentSubjectValueJpaEntity;
import org.flickit.assessment.core.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assessment_assessmentresult")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AssessmentResultJpaEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assessment_id", referencedColumnName = "id", nullable = false)
    private AssessmentJpaEntity assessment;
    @OneToMany(mappedBy = "assessmentResult")
    private List<QualityAttributeValueJpaEntity> qualityAttributeValues;
    @OneToMany(mappedBy = "assessmentResult")
    private List<AssessmentSubjectValueJpaEntity> assessmentSubjectValues;
    @Column(name = "is_valid")
    private boolean isValid;

    public List<AnswerJpaEntity> getAnswers() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return id.toString();
    }

}
