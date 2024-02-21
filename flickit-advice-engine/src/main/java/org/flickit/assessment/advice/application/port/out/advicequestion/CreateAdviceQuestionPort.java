package org.flickit.assessment.advice.application.port.out.advicequestion;

import org.flickit.assessment.advice.application.port.in.advice.CreateAdviceUseCase;
import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

public interface CreateAdviceQuestionPort {

    /**
     * @throws ResourceNotFoundException if no advice found by the given adviceId
     */
    void persistAll(UUID adviceId, List<CreateAdviceUseCase.AdviceQuestion> adviceQuestions);
}
