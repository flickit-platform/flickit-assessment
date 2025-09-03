package org.flickit.assessment.advice.application.port.out.adviceitem;

import java.util.UUID;

public interface LoadAdviceItemPort {

    boolean existsByAssessmentResultId(UUID assessmentResultId);
}
