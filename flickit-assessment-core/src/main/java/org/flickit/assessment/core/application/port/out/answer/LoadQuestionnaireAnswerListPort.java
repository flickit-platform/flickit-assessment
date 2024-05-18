package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.Answer;

import java.util.UUID;

public interface LoadQuestionnaireAnswerListPort {

    PaginatedResponse<Answer> loadQuestionnaireAnswers(UUID assessmentId, long questionnaireId, int size, int page);
}
