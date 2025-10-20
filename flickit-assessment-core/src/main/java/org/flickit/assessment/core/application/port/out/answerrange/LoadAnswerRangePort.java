package org.flickit.assessment.core.application.port.out.answerrange;

import java.util.Set;

public interface LoadAnswerRangePort {

    Set<Long> loadIdsByKitVersionId(Long kitVersionId);
}
