package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.hibernate.annotations.GenericGenerator;

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

    @Column(name = "is_valid")
    private Boolean isValid;
    @ManyToOne(cascade = CascadeType.ALL)

    @OneToMany(mappedBy = "assessmentResult")
    private List<QualityAttributeValueJpaEntity> qualityAttributeValues;

    @OneToMany(mappedBy = "assessmentResult")
    private List<AssessmentSubjectValueJpaEntity> assessmentSubjectValues;


    public List<AnswerJpaEntity> getAnswers() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return id.toString();
    }

}
