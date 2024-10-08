package org.flickit.assessment.kit.application.service.questionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.questionimpact.DeleteQuestionImpactUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteQuestionImpactService implements DeleteQuestionImpactUseCase {

    @Override
    public void delete(Param param) {

    }
}
