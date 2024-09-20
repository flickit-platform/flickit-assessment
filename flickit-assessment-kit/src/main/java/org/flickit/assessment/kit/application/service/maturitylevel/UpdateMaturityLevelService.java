package org.flickit.assessment.kit.application.service.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateMaturityLevelService implements UpdateMaturityLevelUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;
    private final UpdateMaturityLevelPort updateMaturityLevelPort;

    @Override
    public void updateMaturityLevel(Param param) {
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (expertGroup == null)
            throw new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND);

        if (!Objects.equals(expertGroup.getOwnerId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var kitVersionId = loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId());
        var maturityLevel = new MaturityLevel(param.getId(), MaturityLevel.generateSlugCode(param.getTitle()),
            param.getTitle(), param.getIndex(), param.getDescription(), param.getValue(), null);
        updateMaturityLevelPort.updateInfo(maturityLevel, kitVersionId, LocalDateTime.now(), param.getCurrentUserId());
    }
}
