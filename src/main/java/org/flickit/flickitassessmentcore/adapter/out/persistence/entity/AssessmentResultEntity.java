package org.flickit.flickitassessmentcore.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.flickit.flickitassessmentcore.domain.Answer;
import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;
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
public class AssessmentResultEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assessment_id", referencedColumnName = "id", nullable = false)
    private AssessmentEntity assessment;

    public List<AnswerEntity> getAnswers() {
        return new ArrayList<>();
    }

    public List<QualityAttributeValueEntity> getQualityAttributeValues() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return id.toString();
    }

}
