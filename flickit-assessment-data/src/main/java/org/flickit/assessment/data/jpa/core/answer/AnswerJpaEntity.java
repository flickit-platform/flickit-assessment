package org.flickit.assessment.data.jpa.core.answer;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "fac_answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnswerJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

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

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    @Override
    public String toString() {
        return id.toString();
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String QUESTION_ID = "questionId";
        public static final String QUESTION_INDEX = "questionIndex";
    }
}
