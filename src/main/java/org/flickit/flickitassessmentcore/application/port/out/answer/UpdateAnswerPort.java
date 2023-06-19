package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.UUID;

public interface UpdateAnswerPort {

    UUID update(Param param);

    record Param(UUID id,
                 UUID assessmentResultId,
                 Long questionId,
                 Long answerOptionId) {
    }
}
