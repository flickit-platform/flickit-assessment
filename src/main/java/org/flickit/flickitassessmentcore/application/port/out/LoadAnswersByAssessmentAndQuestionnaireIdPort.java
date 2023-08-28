package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase.AnswerListItem;

import java.util.UUID;

public interface LoadAnswersByAssessmentAndQuestionnaireIdPort {

    PaginatedResponse<AnswerListItem> loadAnswersByAssessmentAndQuestionnaireIdPort(Param param);

    record Param(UUID assessmentId, Long questionnaireId, int page, int size) {
    }
}
