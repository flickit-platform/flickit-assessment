package org.flickit.assessment.kit.application.service.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
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

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateMaturityLevelPort updateMaturityLevelPort;

    @Override
    public void updateMaturityLevel(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        var expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());

        if (!Objects.equals(expertGroupOwnerId, param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var maturityLevel = new MaturityLevel(param.getMaturityLevelId(), MaturityLevel.generateSlugCode(param.getTitle()),
            param.getTitle(), param.getIndex(), param.getDescription(), param.getValue(), null);
        updateMaturityLevelPort.update(maturityLevel, param.getKitVersionId(), LocalDateTime.now(), param.getCurrentUserId());
    }
}
