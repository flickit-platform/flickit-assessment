package org.flickit.assessment.kit.application.port.out.answerrange;

import org.flickit.assessment.kit.application.domain.AnswerRange;

public interface LoadAnswerRangePort {

    AnswerRange load(long id, long kitVersionId);
}
