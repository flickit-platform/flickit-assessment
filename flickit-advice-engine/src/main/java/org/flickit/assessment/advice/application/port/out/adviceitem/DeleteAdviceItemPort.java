package org.flickit.assessment.advice.application.port.out.adviceitem;

import java.util.UUID;

public interface DeleteAdviceItemPort {

    void deleteAllAiGenerated(UUID assessmentResultId);
}
