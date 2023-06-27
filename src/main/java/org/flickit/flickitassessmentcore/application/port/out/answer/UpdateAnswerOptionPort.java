package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.UUID;

public interface UpdateAnswerOptionPort {

    void updateAnswerOptionById(Param param);

    record Param(UUID id, Long answerOptionId) {
    }
}
