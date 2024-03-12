package org.flickit.assessment.core.application.port.out.question;

import java.util.UUID;

public interface CheckQuestionKitExistencePort {

    boolean existsByRefNumAndAssessmentId(UUID refNum, UUID assessmentId);
}
