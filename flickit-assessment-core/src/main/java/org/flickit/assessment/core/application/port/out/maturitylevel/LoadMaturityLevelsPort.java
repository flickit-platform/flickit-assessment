package org.flickit.assessment.core.application.port.out.maturitylevel;

import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.util.List;
import java.util.UUID;

public interface LoadMaturityLevelsPort {

    List<MaturityLevel> loadAllByKitVersion(Long kitVersionId);

    List<MaturityLevel> loadAllByAssessment(UUID assessmentId);

    List<MaturityLevel> loadAllTranslated(AssessmentResult assessmentResult);
}
