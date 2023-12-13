package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessment.DeleteAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.assessment.core.application.port.out.assessment.DeleteAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_ASSESSMENT_ID_NOT_FOUND;

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
        deleteAssessmentPort.deleteById(param.getId(), deletionTime);
    }
}
