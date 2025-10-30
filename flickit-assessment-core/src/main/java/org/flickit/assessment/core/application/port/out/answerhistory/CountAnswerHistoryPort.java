package org.flickit.assessment.core.application.port.out.answerhistory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CountAnswerHistoryPort {

    Map<Long, Integer> countAnswerHistories(UUID assessmentId, List<Long> questionIds);
}
