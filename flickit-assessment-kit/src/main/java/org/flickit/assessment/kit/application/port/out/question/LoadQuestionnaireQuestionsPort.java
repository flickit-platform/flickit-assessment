package org.flickit.assessment.kit.application.port.out.question;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.Question;

public interface LoadQuestionnaireQuestionsPort {

    PaginatedResponse<Question> loadQuestionnaireQuestions(Param param);

    record Param(long questionnaireId, long kitVersionId, int page, int size) {}
}
