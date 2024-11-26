package org.flickit.assessment.advice.application.port.out.assessment;

import org.flickit.assessment.advice.application.domain.Assessment;
import org.flickit.assessment.common.application.domain.ID;

import java.util.UUID;

public interface LoadAssessmentPort {

    Assessment loadById (ID assessmentId);
}
