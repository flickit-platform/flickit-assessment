package org.flickit.assessment.data.jpa.kit.answerrange;

import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;

public interface AnswerRangeJoinOptionView {

    AnswerRangeJpaEntity getAnswerRange();

    AnswerOptionJpaEntity getAnswerOption();
}
