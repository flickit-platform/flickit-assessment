package org.flickit.assessment.kit.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateKitAttributeUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitByVersionIdPort;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionStatusByIdPort;
import org.flickit.assessment.kit.application.port.out.kitversion.UpdateKitVersionModificationInfoPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_NOT_UPDATING_STATUS;

@Service
@RequiredArgsConstructor
public class UpdateKitAttributeService implements UpdateKitAttributeUseCase {

    private final LoadAssessmentKitByVersionIdPort loadAssessmentKitByVersionIdPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadKitVersionStatusByIdPort loadKitVersionStatusByIdPort;
    private final UpdateAttributePort updateAttributePort;
    private final UpdateKitVersionModificationInfoPort updateKitVersionModificationInfoPort;

    @Override
    public void updateKitAttribute(Param param) {
        var assessmentKit = loadAssessmentKitByVersionIdPort.loadByVersionId(param.getKitVersionId());
        checkUserAccess(assessmentKit.getExpertGroupId(), param.getCurrentUserId());
        checkKitVersionStatus(param);
        updateAttributePort.update(toParam(param));
        updateKitVersionModificationInfoPort.updateModificationInfo(param.getKitVersionId(), LocalDateTime.now(), param.getCurrentUserId());
    }

    private void checkUserAccess(Long expertGroupId, UUID currentUserId) {
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(currentUserId, ownerId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private void checkKitVersionStatus(Param param) {
        var status = loadKitVersionStatusByIdPort.loadStatusById(param.getKitVersionId());
        if (!Objects.equals(status, KitVersionStatus.UPDATING)) {
            throw new ValidationException(KIT_VERSION_NOT_UPDATING_STATUS);
        }
    }

    private UpdateAttributePort.Param toParam(Param param) {
        return new UpdateAttributePort.Param(param.getAttributeId(),
            param.getKitVersionId(),
            param.getCode(),
            param.getTitle(),
            param.getIndex(),
            param.getDescription(),
            param.getWeight(),
            LocalDateTime.now(),
            param.getCurrentUserId(),
            param.getSubjectId());
    }
}
