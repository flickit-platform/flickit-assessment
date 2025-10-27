package org.flickit.assessment.core.application.port.out.question;

import java.util.Set;
import java.util.UUID;

public interface LoadQuestionPort {

    int loadFirstUnansweredQuestionIndex(long questionnaireId, UUID assessmentResultId);

    Set<Long> loadIdsByKitVersionId(long kitVersionId);
}
