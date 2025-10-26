package org.flickit.assessment.data.jpa.core.answer;

import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;

public interface AnswerWithOptionView {

    AnswerJpaEntity getAnswer();

    AnswerOptionJpaEntity getOption();
}
