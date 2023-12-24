package org.flickit.assessment.data.jpa.kit.question;

import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

public interface QuestionJoinAnswerView {

    String getQuestionnaireTitle();

    Long getQuestionId();

    Integer getQuestionIndex();

    String getQuestionTitle();

    AnswerJpaEntity getAnswer();

    QuestionImpactJpaEntity getQuestionImpact();

    AnswerOptionImpactJpaEntity getOptionImpact();

    Integer getOptionIndex();

    String getOptionTitle();
}
