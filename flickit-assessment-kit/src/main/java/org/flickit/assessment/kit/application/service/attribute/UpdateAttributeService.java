package org.flickit.assessment.kit.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
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
import static org.flickit.assessment.common.util.GenerateCodeUtil.generateCode;

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
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!Objects.equals(param.getCurrentUserId(), ownerId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateAttributePort.update(toParam(param));
    }

    private UpdateAttributePort.Param toParam(Param param) {
        return new UpdateAttributePort.Param(param.getAttributeId(),
            param.getKitVersionId(),
            generateCode(param.getTitle()),
            param.getTitle(),
            param.getIndex(),
            param.getDescription(),
            param.getWeight(),
            LocalDateTime.now(),
            param.getCurrentUserId(),
            param.getSubjectId());
    }
}
