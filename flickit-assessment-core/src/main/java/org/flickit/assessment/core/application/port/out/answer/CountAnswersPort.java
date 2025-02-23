package org.flickit.assessment.core.application.port.out.answer;

import java.util.List;
import java.util.UUID;

public interface CountAnswersPort {

    int countByQuestionIds(UUID assessmentResultId, List<Long> questionIds);

    int countUnapprovedAnswers(UUID assessmentResultId);
}
