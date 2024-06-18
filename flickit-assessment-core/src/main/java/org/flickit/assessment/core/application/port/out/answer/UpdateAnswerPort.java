package org.flickit.assessment.core.application.port.out.answer;

import java.util.UUID;

public interface UpdateAnswerPort {

    void update(Param param);

    record Param(UUID answerId, Long answerOptionId, Integer confidenceLevelId, Boolean isNotApplicable,
                 UUID currentUserId) {
    }
}
