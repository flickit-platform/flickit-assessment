package org.flickit.assessment.data.jpa.kit.attribute;

import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;

public interface AttributeQuestionView {

    long getQuestionId();

    Double getQuestionWeight();

    AnswerJpaEntity getAnswer();

    AnswerOptionImpactJpaEntity getOptionImpact();

    Double getOptionValue();

    Boolean getAnswerIsNotApplicable();
}
