package org.flickit.assessment.data.jpa.core.answerhistory;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fac_answer_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnswerHistoryJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "answer_id", referencedColumnName = "id", nullable = false)
    private AnswerJpaEntity answer;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assessment_result_id", referencedColumnName = "id", nullable = false)
    private AssessmentResultJpaEntity assessmentResult;

    @Column(name = "questionnaire_id", nullable = false)
    private Long questionnaireId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "answer_option_id")
    private Long answerOptionId;

    @Column(name = "confidence_level_id")
    private Integer confidenceLevelId;

    @Column(name = "is_not_applicable")
    private Boolean isNotApplicable;

    @Column(name = "modified_by", nullable = false)
    private UUID modifiedBy;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Override
    public String toString() {
        return id.toString();
    }
}
