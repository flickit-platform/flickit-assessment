package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.UUID;

public interface LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort {

    Result loadByAssessmentResultIdAndQuestionId(UUID assessmentResultId, Long questionId);

    record Result(UUID id, Long answerOptionId) {
    }
}
