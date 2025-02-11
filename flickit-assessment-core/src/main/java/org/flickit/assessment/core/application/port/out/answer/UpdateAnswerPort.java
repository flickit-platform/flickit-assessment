package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.core.application.domain.AnswerStatus;

import java.util.UUID;

public interface UpdateAnswerPort {

    void update(Param param);

    record Param(UUID answerId,
                 Long answerOptionId,
                 Integer confidenceLevelId,
                 Boolean isNotApplicable,
                 AnswerStatus status,
                 UUID currentUserId) {
    }
}
