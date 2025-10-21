package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.core.application.domain.Answer;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LoadAnswerPort {

    Optional<Answer> load(UUID assessmentResultId, Long questionId);

    List<Answer> loadAllUnapproved(UUID assessmentResultId);

    Set<UUID> loadIdsByQuestionIds(List<Long> questionIds);
}
