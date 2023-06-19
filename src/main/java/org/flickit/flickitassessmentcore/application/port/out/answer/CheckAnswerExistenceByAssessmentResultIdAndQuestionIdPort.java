package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.UUID;

public interface CheckAnswerExistenceByAssessmentResultIdAndQuestionIdPort {
    boolean existsByAssessmentResultIdAndQuestionId(UUID assessmentResultId, Long questionId);
}
