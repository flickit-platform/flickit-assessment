package org.flickit.assessment.core.application.port.out.answerhistory;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AnswerHistory;

import java.util.UUID;

public interface LoadAnswerHistoryListPort {

    PaginatedResponse<AnswerHistory> load(UUID assessmentId, long questionId, int page, int size);
}
