package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.port.out.adviceitem.UpdateAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.advice.application.port.in.adviceitem.UpdateAdviceItemUseCase;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAdviceItemService implements UpdateAdviceItemUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final UpdateAdviceItemPort updateAdviceItemPort;

    @Override
    public void updateAdviceItem(Param param) {

    }
}
