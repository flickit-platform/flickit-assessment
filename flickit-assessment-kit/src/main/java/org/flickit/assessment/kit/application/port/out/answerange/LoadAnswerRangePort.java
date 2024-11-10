package org.flickit.assessment.kit.application.port.out.answerange;

import org.flickit.assessment.kit.application.domain.AnswerRange;

import java.util.Optional;

public interface LoadAnswerRangePort {

    Optional<AnswerRange> loadAnswerRange(long id, long kitVersionId);
}
