package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.UUID;

public interface UpdateAnswerPort {

    void update(Param param);

    record Param(UUID answerId, Long answerOptionId, Boolean isNotApplicable) {
    }
}
