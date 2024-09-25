package org.flickit.assessment.kit.application.service.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.maturitylevel.DeleteMaturityLevelUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteMaturityLevelService implements DeleteMaturityLevelUseCase {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final DeleteMaturityLevelPort deleteMaturityLevelPort;

    @Override
    public void delete(Param param) {
        var assessmentKit = loadAssessmentKitPort.load(param.getKitId());
        var expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId());

        if (!expertGroupOwnerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        deleteMaturityLevelPort.delete(param.getMaturityLevelId(), assessmentKit.getKitVersionId());
    }
}
