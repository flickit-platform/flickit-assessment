package org.flickit.assessment.kit.application.service.answeroptions;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.answeroptions.UpdateAnswerOptionOrdersUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAnswerOptionOrdersService implements UpdateAnswerOptionOrdersUseCase {

    @Override
    public void changeOrders(Param param) {

    }
}
