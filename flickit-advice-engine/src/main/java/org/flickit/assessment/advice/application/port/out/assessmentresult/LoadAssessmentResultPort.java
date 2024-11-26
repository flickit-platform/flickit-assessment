package org.flickit.assessment.advice.application.port.out.assessmentresult;

import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.common.application.domain.ID;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentResultPort {

    Optional<AssessmentResult> loadByAssessmentId(ID assessmentId);
}
