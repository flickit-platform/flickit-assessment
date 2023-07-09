package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.RemoveAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.RemoveAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RemoveAssessmentService implements RemoveAssessmentUseCase {

    private final RemoveAssessmentPort removeAssessmentPort;

    @Override
    public void removeAssessment(Param param) {
        removeAssessmentPort.removeById(param.getId());
    }
}
