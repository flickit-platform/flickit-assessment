package org.flickit.assessment.kit.application.service.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateMaturityLevelService implements UpdateMaturityLevelUseCase {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateMaturityLevelPort updateMaturityLevelPort;

    @Override
    public void updateMaturityLevel(Param param) {
        var assessmentKit = loadAssessmentKitPort.load(param.getKitId());
        var expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId());

        if (!Objects.equals(expertGroupOwnerId, param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var maturityLevel = new MaturityLevel(param.getId(), MaturityLevel.generateSlugCode(param.getTitle()),
            param.getTitle(), param.getIndex(), param.getDescription(), param.getValue(), null);
        updateMaturityLevelPort.updateInfo(maturityLevel, assessmentKit.getKitVersionId(), LocalDateTime.now(), param.getCurrentUserId());
    }
}
