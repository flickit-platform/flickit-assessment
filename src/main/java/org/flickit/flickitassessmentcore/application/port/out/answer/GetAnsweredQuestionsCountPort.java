package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.UUID;

public interface GetAnsweredQuestionsCountPort {
    Result getAnsweredQuestionsCountById(UUID assessmentId);

    record Result(UUID id, Integer allAnswersCount) {
    }
}
