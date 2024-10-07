package org.flickit.assessment.kit.application.service.questionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.questionimpact.CreateQuestionImpactUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateQuestionImpactService implements CreateQuestionImpactUseCase {

    @Override
    public Result createQuestionImpact(Param param) {
        return null;
    }
}
