package org.flickit.flickitassessmentcore.application.port.out.answer;

import org.flickit.flickitassessmentcore.application.domain.Answer;

import java.util.Optional;
import java.util.UUID;

public interface LoadAnswerPort {

    Optional<Answer> load(UUID assessmentResultId, Long questionId);

}
