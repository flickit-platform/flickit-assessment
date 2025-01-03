package org.flickit.assessment.core.application.port.out.questionnaire;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;

import java.util.UUID;

public interface LoadQuestionnairesByAssessmentIdPort {

    PaginatedResponse<QuestionnaireListItem> loadAllByAssessmentId(Param param);

    record Param(UUID assessmentId, AssessmentResult assessmentResult, int size, int page) {
    }
}
