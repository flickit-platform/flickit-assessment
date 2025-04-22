package org.flickit.assessment.data.jpa.kit.attribute;

import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

public interface ImpactFullQuestionsView {

    Long getQuestionnaireId();

    String getQuestionnaireTitle();

    String getQuestionnaireTranslations();

    Long getQuestionId();

    Integer getQuestionIndex();

    String getQuestionTitle();

    String getQuestionTranslations();

    AnswerJpaEntity getAnswer();

    QuestionImpactJpaEntity getQuestionImpact();

    Integer getOptionIndex();

    String getOptionTitle();

    String getOptionTranslations();

    Double getGainedScore();

    Double getMissedScore();

    int getEvidenceCount();
}
