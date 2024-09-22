package org.flickit.assessment.advice.application.port.out.advicenarration;

import org.flickit.assessment.advice.application.domain.AdviceNarration;

public interface UpdateAdviceNarrationPort {

    void updateAiNarration(AdviceNarration adviceNarration);

    void updateAssessorNarration(AdviceNarration adviceNarration);
}
