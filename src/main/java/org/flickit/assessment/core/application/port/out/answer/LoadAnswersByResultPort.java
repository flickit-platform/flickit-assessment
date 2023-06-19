package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.core.domain.Answer;

import java.util.Set;
import java.util.UUID;

public interface LoadAnswersByResultPort {

    Set<Answer> loadAnswersByResultId(UUID resultId);
}
