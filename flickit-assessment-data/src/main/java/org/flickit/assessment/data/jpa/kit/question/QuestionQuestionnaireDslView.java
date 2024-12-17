package org.flickit.assessment.data.jpa.kit.question;

import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;

import java.util.List;

public interface QuestionQuestionnaireDslView {

    String getAnswerRangeCode();

    QuestionJpaEntity getQuestion();

    List<QuestionImpactJpaEntity> getQuestionImpacts();

    QuestionnaireJpaEntity getQuestionnaire();

    List<AnswerOptionJpaEntity> getAnswerOptions();

    List<AttributeJpaEntity> getAttributes();
}
