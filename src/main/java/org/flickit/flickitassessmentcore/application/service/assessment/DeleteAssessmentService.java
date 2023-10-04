package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.DeleteAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.DeleteAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.DELETE_ASSESSMENT_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAssessmentService implements DeleteAssessmentUseCase {

    private final DeleteAssessmentPort deleteAssessmentPort;
    private final CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Override
    public void deleteAssessment(Param param) {
        if (!checkAssessmentExistencePort.existsById(param.getId()))
            throw new ResourceNotFoundException(DELETE_ASSESSMENT_ID_NOT_FOUND);
        long deletionTime = System.currentTimeMillis();
        deleteAssessmentPort.setDeletionTimeById(param.getId(), deletionTime);
    }
}
