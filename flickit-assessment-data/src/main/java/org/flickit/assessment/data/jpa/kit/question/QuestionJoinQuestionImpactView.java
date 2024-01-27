package org.flickit.assessment.data.jpa.kit.question;

import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

public interface QuestionJoinQuestionImpactView {

    QuestionJpaEntity getQuestion();

    QuestionImpactJpaEntity getQuestionImpact();

}
