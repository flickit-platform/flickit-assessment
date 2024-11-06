package org.flickit.assessment.kit.application.port.out.answerange;

import org.flickit.assessment.kit.application.domain.AnswerRange;

public interface LoadAnswerRangePort {

    AnswerRange loadById(long kitVersionId, long id);
}
