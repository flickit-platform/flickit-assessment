package org.flickit.assessment.kit.application.service.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.levelcompetence.CreateLevelCompetenceUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitVersionExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionStatusByIdPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelPort;
import org.flickit.assessment.kit.common.ErrorMessageKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_NOT_UPDATING_STATUS;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateLevelCompetenceService implements CreateLevelCompetenceUseCase {

    private final LoadKitVersionExpertGroupPort loadKitVersionExpertGroupPort;
    private final LoadKitVersionStatusByIdPort loadKitVersionStatusByIdPort;
    private final LoadMaturityLevelPort loadMaturityLevelPort;
    private final CreateLevelCompetencePort createLevelCompetencePort;

    @Override
    public void createLevelCompetence(Param param) {
        checkUserAccess(param.getKitVersionId(), param.getCurrentUserId());
        checkKitVersionStatus(param.getKitVersionId());
        checkLevelsExistence(param);
        createLevelCompetencePort.persist(param.getAffectedLevelId(),
            param.getEffectiveLevelId(),
            param.getValue(),
            param.getKitVersionId(),
            param.getCurrentUserId());
    }

    private void checkUserAccess(Long kitVersionId, UUID currentUserId) {
        ExpertGroup expertGroup = loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(kitVersionId);
        if (!Objects.equals(currentUserId, expertGroup.getOwnerId())) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }

    private void checkKitVersionStatus(Long kitVersionId) {
        KitVersionStatus status = loadKitVersionStatusByIdPort.loadStatusById(kitVersionId);
        if (!Objects.equals(status, KitVersionStatus.UPDATING)) {
            throw new ValidationException(KIT_VERSION_NOT_UPDATING_STATUS);
        }
    }

    private void checkLevelsExistence(Param param) {
        MaturityLevel affectedLevel = loadMaturityLevelPort.loadByIdAndKitVersionId(param.getAffectedLevelId(), param.getKitVersionId());
        MaturityLevel effectiveLevel = loadMaturityLevelPort.loadByIdAndKitVersionId(param.getEffectiveLevelId(), param.getKitVersionId());
        if (affectedLevel == null || effectiveLevel == null) {
            throw new ResourceNotFoundException(ErrorMessageKey.MATURITY_LEVEL_ID_NOT_FOUND);
        }
    }
}
