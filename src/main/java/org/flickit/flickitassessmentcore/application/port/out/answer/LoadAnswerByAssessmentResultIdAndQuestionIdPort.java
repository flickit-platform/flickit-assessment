package org.flickit.flickitassessmentcore.application.port.out.answer;

import org.flickit.flickitassessmentcore.domain.Answer;

import java.util.UUID;

public interface LoadAnswerByAssessmentResultIdAndQuestionIdPort {
    Answer loadByAssessmentResultIdAndQuestionId(UUID assessmentResultId, Long questionId);
}
