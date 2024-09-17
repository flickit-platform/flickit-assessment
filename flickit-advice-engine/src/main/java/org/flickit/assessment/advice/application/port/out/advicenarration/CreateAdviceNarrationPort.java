package org.flickit.assessment.advice.application.port.out.advicenarration;

import org.flickit.assessment.advice.application.domain.AdviceNarration;

public interface CreateAdviceNarrationPort {

    void persist(AdviceNarration adviceNarration);
}
