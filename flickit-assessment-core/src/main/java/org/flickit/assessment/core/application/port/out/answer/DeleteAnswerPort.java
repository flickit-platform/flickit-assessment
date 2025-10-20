package org.flickit.assessment.core.application.port.out.answer;

import java.util.Set;
import java.util.UUID;

public interface DeleteAnswerPort {

    void deleteSelectedOptionFromAnswers(Set<UUID> answerIds, UUID modifiedBy);
}
