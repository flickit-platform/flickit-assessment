package org.flickit.assessment.core.application.port.out.adviceitem;

import java.util.UUID;

public interface DeleteAdviceItemPort {

    void delete(UUID id);

    void deleteAllAiGenerated(UUID assessmentResultId);
}
