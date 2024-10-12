package org.flickit.assessment.kit.application.service.question;

import lombok.NoArgsConstructor;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionImpactsUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@NoArgsConstructor
public class GetQuestionImpactsService implements GetQuestionImpactsUseCase {

    @Override
    public Result getQuestionImpacts(Param param) {
        return null;
    }
}
