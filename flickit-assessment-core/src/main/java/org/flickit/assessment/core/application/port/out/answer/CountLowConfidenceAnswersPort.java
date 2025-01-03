package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.core.application.domain.ConfidenceLevel;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public interface CountLowConfidenceAnswersPort {

    int countWithConfidenceLessThan(UUID assessmentResultId, ConfidenceLevel confidence);

    Map<Long, Integer> countByQuestionnaireIdWithConfidenceLessThan(UUID assessmentResultId, ArrayList<Long> questionnaireId, ConfidenceLevel confidence);
}
