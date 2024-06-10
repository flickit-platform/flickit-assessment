package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.Answer;

import java.util.UUID;

public interface LoadAnswerListPort {

    PaginatedResponse<Answer> loadByQuestionnaire(UUID assessmentId, long questionnaireId, int size, int page);
}
