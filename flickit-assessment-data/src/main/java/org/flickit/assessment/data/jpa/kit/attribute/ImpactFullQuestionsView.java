package org.flickit.assessment.data.jpa.kit.attribute;

import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

public interface ImpactFullQuestionsView {

    String getQuestionnaireTitle();

    Long getQuestionId();

    Integer getQuestionIndex();

    String getQuestionTitle();

    AnswerJpaEntity getAnswer();

    QuestionImpactJpaEntity getQuestionImpact();

    Integer getOptionIndex();

    String getOptionTitle();

    Double getAnswerScore();

    Double getWeightedScore();

    int getEvidenceCount();
}
