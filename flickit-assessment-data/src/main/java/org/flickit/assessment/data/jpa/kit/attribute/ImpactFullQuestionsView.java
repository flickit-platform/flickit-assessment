package org.flickit.assessment.data.jpa.kit.attribute;

import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

public interface ImpactFullQuestionsView {

    String getQuestionnaireTitle();

    Long getQuestionId();

    Integer getQuestionIndex();

    String getQuestionTitle();

    AnswerJpaEntity getAnswer();

    QuestionImpactJpaEntity getQuestionImpact();

    AnswerOptionImpactJpaEntity getOptionImpact();

    AnswerOptionJpaEntity getOption();
}
