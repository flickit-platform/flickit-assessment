package org.flickit.assessment.data.jpa.kit.question;

import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;

public interface AttributeLevelImpactfulQuestionsView {

    QuestionJpaEntity getQuestion();

    QuestionnaireJpaEntity getQuestionnaire();

    QuestionImpactJpaEntity getQuestionImpact();

    AnswerOptionImpactJpaEntity getOptionImpact();

    AnswerOptionJpaEntity getAnswerOption();
}
