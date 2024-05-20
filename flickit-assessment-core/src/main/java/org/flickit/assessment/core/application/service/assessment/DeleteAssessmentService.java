package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.DeleteAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.assessment.DeleteAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAssessmentService implements DeleteAssessmentUseCase {

    private final DeleteAssessmentPort deleteAssessmentPort;
    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Override
    public void deleteAssessment(Param param) {
        if (!checkUserAssessmentAccessPort.hasAccess(param.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        long deletionTime = System.currentTimeMillis();
        deleteAssessmentPort.deleteById(param.getId(), deletionTime);
    }
}
