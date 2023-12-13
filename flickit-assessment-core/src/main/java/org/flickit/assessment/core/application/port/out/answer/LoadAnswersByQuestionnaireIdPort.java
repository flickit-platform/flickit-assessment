package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.port.in.answer.GetAnswerListUseCase.AnswerListItem;

import java.util.UUID;

public interface LoadAnswersByQuestionnaireIdPort {

    PaginatedResponse<AnswerListItem> loadAnswersByQuestionnaireId(Param param);

    record Param(UUID assessmentId, Long questionnaireId, int page, int size) {
    }
}
