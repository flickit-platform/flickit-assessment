package org.flickit.assessment.core.application.port.out.question;

import org.flickit.assessment.core.application.domain.Question;

import java.util.UUID;

public interface LoadQuestionPort {

    Question loadByIdAndKitVersionId(Long id, Long kitVersionId);

    int loadNextUnansweredQuestionIndex(long questionnaireId, UUID assessmentResultId);
}
