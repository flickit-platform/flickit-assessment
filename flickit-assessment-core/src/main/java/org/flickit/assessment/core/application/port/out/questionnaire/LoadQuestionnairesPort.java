package org.flickit.assessment.core.application.port.out.questionnaire;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;

import java.util.List;
import java.util.UUID;

public interface LoadQuestionnairesPort {

    PaginatedResponse<QuestionnaireListItem> loadAllByAssessmentId(Param param);

    record Param(AssessmentResult assessmentResult, int size, int page) {
    }

    List<Result> loadQuestionnaireDetails(long kitVersionId, UUID assessmentResultId);

    record Result(long id, int index, String title, Integer nextQuestionIndex,int questionCount, int answerCount) {
    }
}
