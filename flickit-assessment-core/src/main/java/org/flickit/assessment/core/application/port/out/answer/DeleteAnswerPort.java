package org.flickit.assessment.core.application.port.out.answer;

import java.util.Set;
import java.util.UUID;

public interface DeleteAnswerPort {

    void delete(UUID assessmentResultId, Set<Long> questionIds);

    void deleteSelectedOption(Set<UUID> answerIds, UUID modifiedBy);
}
