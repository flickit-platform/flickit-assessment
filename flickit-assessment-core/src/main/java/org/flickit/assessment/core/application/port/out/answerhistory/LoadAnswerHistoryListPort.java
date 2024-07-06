package org.flickit.assessment.core.application.port.out.answerhistory;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LoadAnswerHistoryListPort {

    PaginatedResponse<AnswerHistoryListItem> loadByAssessmentIdAndQuestionId(UUID assessmentId,
                                                                             long questionId,
                                                                             int page,
                                                                             int size);

    record AnswerHistoryListItem(
        LocalDateTime submitTime,
        String submitterName,
        String confidenceLevel,
        Integer answerOptionIndex,
        Boolean isNotApplicable) {}
}
