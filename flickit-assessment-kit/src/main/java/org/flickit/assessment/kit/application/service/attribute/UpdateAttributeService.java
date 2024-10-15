package org.flickit.assessment.kit.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.common.util.SlugCodeUtil;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeUseCase;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_NOT_UPDATING_STATUS;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAttributeService implements UpdateAttributeUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateAttributePort updateAttributePort;

    @Override
    public void updateAttribute(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        checkUserAccess(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId());
        checkKitVersionStatus(kitVersion);
        updateAttributePort.update(toParam(param));
    }

    private void checkUserAccess(Long expertGroupId, UUID currentUserId) {
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(currentUserId, ownerId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private void checkKitVersionStatus(KitVersion kitVersion) {
        if (!Objects.equals(kitVersion.getStatus(), KitVersionStatus.UPDATING)) {
            throw new ValidationException(KIT_VERSION_NOT_UPDATING_STATUS);
        }
    }

    private UpdateAttributePort.Param toParam(Param param) {
        return new UpdateAttributePort.Param(param.getAttributeId(),
            param.getKitVersionId(),
            SlugCodeUtil.generateSlugCode(param.getTitle()),
            param.getTitle(),
            param.getIndex(),
            param.getDescription(),
            param.getWeight(),
            LocalDateTime.now(),
            param.getCurrentUserId(),
            param.getSubjectId());
    }
}
