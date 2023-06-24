package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentsubjectvalue.AssessmentSubjectValueJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assessment_assessmentresult")
@Getter
@Setter
@ToString
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

    @OneToMany(mappedBy = "assessmentResult")
    private List<QualityAttributeValueJpaEntity> qualityAttributeValues;

    @OneToMany(mappedBy = "assessmentResult")
    private List<AssessmentSubjectValueJpaEntity> assessmentSubjectValues;

    @Column(name = "is_valid")
    private boolean isValid;
}
