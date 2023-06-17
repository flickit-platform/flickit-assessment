package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.UUID;

public interface SaveAnswerPort {

    UUID persist(Param param);

    record Param(UUID assessmentResultId,
                 Long questionId,
                 Long answerOptionId) {
    }
}
