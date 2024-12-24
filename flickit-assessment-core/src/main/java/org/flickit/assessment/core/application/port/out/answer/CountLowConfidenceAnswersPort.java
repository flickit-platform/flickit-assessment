package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.core.application.domain.ConfidenceLevel;

import java.util.UUID;

public interface CountLowConfidenceAnswersPort {

    int countWithConfidenceLessThan(UUID assessmentResultId, ConfidenceLevel confidence);
}
