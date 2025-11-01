package org.flickit.assessment.core.application.port.out.question;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.Question;

public interface LoadQuestionnaireQuestionListPort {

    PaginatedResponse<Question> loadByQuestionnaireId(LoadQuestionsParam param);

    record LoadQuestionsParam(Long questionnaireId, long kitVersionId, int langId, int size, int page) {
    }
}
