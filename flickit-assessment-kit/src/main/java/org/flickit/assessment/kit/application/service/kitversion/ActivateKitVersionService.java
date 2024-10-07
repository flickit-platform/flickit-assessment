package org.flickit.assessment.kit.application.service.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.kitversion.ActivateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitActiveVersionPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.kitversion.UpdateKitVersionStatusPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivateKitVersionService implements ActivateKitVersionUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateKitVersionStatusPort updateKitVersionStatusPort;
    private final UpdateKitActiveVersionPort updateKitActiveVersionPort;

    @Override
    public void activateKitVersion(Param param) {
        var kit = loadKitVersionPort.load(param.getKitVersionId()).getKit();
        var ownerId = loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (kit.getKitVersionId() != null)
            updateKitVersionStatusPort.updateStatus(kit.getKitVersionId(), KitVersionStatus.ARCHIVE);
        updateKitVersionStatusPort.updateStatus(param.getKitVersionId(), KitVersionStatus.ACTIVE);
        updateKitActiveVersionPort.updateActiveVersion(kit.getId(), param.getKitVersionId());
    }
}
