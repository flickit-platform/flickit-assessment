package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.domain.Answer;

import java.util.List;
import java.util.UUID;

public interface LoadAnswersByAssessmentAndQuestionIdsPort {

    List<Answer> loadAnswersByAssessmentAndQuestionIdsPort(Param param);

    record Param(UUID assessmentId, List<Long> questionIds) {
    }
}
