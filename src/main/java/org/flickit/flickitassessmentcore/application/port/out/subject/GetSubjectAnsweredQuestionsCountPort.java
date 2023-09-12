package org.flickit.flickitassessmentcore.application.port.out.subject;

import java.util.UUID;

public interface GetSubjectAnsweredQuestionsCountPort {

    Result getSubjectAnsweredQuestionsCount(UUID assessmentId, Long subjectId);

    record Result(UUID id, Integer answerCount, Integer questionCount) {
    }
}
