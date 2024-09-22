package org.flickit.assessment.kit.application.service.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.levelcompetence.UpdateLevelCompetenceUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateLevelCompetenceService implements UpdateLevelCompetenceUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;
    private final UpdateLevelCompetencePort updateLevelCompetencePort;
    private final DeleteLevelCompetencePort deleteLevelCompetencePort;

    @Override
    public void updateLevelCompetence(Param param) {
        var kitVersionId = loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId());

        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!expertGroup.getOwnerId().equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (param.getValue() == 0)
            deleteLevelCompetencePort.deleteByIdAndKitVersionId(param.getId(), kitVersionId);
        else
            updateLevelCompetencePort.updateInfo(toParam(param.getId(), kitVersionId, param.getValue(), param.getCurrentUserId()));
    }

    private UpdateLevelCompetencePort.Param toParam(Long id, Long kitVersionId, Integer value, UUID currentUserId) {
        return new UpdateLevelCompetencePort.Param(id, kitVersionId, value, currentUserId, LocalDateTime.now());
    }
}
