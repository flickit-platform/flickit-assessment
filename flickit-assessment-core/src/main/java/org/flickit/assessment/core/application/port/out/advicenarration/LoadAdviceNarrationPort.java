package org.flickit.assessment.core.application.port.out.advicenarration;

import java.util.UUID;

public interface LoadAdviceNarrationPort {

    String load(UUID assessmentResultId);
}
