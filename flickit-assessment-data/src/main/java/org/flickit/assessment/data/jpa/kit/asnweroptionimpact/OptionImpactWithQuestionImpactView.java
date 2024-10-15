package org.flickit.assessment.data.jpa.kit.asnweroptionimpact;

import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

public interface OptionImpactWithQuestionImpactView {

    AnswerOptionImpactJpaEntity getOptionImpact();

    QuestionImpactJpaEntity getQuestionImpact();
}
