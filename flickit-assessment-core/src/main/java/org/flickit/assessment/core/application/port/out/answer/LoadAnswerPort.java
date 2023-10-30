package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.core.application.domain.Answer;

import java.util.Optional;
import java.util.UUID;

public interface LoadAnswerPort {

    Optional<Answer> load(UUID assessmentResultId, Long questionId);

}
