package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.DeleteAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.DeleteAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteAssessmentService implements DeleteAssessmentUseCase {

    private final DeleteAssessmentPort deleteAssessmentPort;

    @Override
    public void deleteAssessment(Param param) {
        long deletionTime = System.currentTimeMillis();
        deleteAssessmentPort.setDeletionTimeById(param.getId(), deletionTime);
    }
}
