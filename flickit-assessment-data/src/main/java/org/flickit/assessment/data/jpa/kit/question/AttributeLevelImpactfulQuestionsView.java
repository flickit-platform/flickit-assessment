package org.flickit.assessment.data.jpa.kit.question;

import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;

public interface AttributeLevelImpactfulQuestionsView {

    QuestionJpaEntity getQuestion();

    QuestionnaireJpaEntity getQuestionnaire();

    QuestionImpactJpaEntity getQuestionImpact();

    AnswerOptionJpaEntity getAnswerOption();
}
