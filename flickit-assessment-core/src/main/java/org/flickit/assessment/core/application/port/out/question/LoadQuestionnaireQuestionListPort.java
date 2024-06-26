package org.flickit.assessment.core.application.port.out.question;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.Question;

public interface LoadQuestionnaireQuestionListPort {

    PaginatedResponse<Question> loadByQuestionnaireId(Long questionnaireId, Long kitVersionId, int size, int page);
}
