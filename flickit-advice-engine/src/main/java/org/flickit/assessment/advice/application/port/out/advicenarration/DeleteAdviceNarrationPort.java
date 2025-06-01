package org.flickit.assessment.advice.application.port.out.advicenarration;

import java.util.UUID;

public interface DeleteAdviceNarrationPort {

    void delete(UUID assessmentResultId);
}
