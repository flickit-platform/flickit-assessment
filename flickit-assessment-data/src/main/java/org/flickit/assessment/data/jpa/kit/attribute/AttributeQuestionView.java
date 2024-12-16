package org.flickit.assessment.data.jpa.kit.attribute;

import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;

public interface AttributeQuestionView {

    long getQuestionId();

    int getQuestionWeight();

    AnswerJpaEntity getAnswer();

    Double getOptionValue();

    Boolean getAnswerIsNotApplicable();
}
