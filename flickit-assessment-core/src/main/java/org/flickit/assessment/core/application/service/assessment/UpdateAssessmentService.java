package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.UPDATE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.Assessment.generateSlugCode;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAssessmentService implements UpdateAssessmentUseCase {

    private final UpdateAssessmentPort updateAssessmentPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public Result updateAssessment(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), UPDATE_ASSESSMENT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        String code = generateSlugCode(param.getTitle());
        LocalDateTime lastModificationTime = LocalDateTime.now();
        UpdateAssessmentPort.AllParam updateParam = new UpdateAssessmentPort.AllParam(
            param.getId(),
            param.getTitle(),
            param.getShortTitle(),
            code,
            lastModificationTime,
            param.getCurrentUserId());

        return new Result(updateAssessmentPort.update(updateParam).id());
    }
}
