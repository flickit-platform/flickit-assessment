package org.flickit.assessment.data.jpa.kit.attribute;

import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

public interface ImpactFullQuestionsView {

    Long getQuestionnaireId();

    String getQuestionnaireTitle();

    String getQuestionnaireTranslation();

    Long getQuestionId();

    Integer getQuestionIndex();

    String getQuestionTitle();

    String getQuestionTranslation();

    AnswerJpaEntity getAnswer();

    QuestionImpactJpaEntity getQuestionImpact();

    Integer getOptionIndex();

    String getOptionTitle();

    String getOptionTranslation();

    Double getGainedScore();

    Double getMissedScore();

    int getEvidenceCount();
}
