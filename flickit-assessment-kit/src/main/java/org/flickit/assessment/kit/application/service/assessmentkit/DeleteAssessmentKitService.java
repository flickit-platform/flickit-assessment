package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.DeleteAssessmentKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitAssessmentsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.DeleteAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_KIT_HAS_ASSESSMENT;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAssessmentKitService implements DeleteAssessmentKitUseCase {

    public final DeleteAssessmentKitPort deleteAssessmentKitPort;
    public final LoadKitExpertGroupPort loadKitExpertGroupPort;
    public final CountKitAssessmentsPort countKitAssessmentsPort;

    @Override
    public void delete(Param param) {
        ExpertGroup expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!expertGroup.getOwnerId().equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (countKitAssessmentsPort.count(param.getKitId()) != 0)
            throw new ValidationException(DELETE_KIT_HAS_ASSESSMENT);

        deleteAssessmentKitPort.delete(param.getKitId());
    }
}
