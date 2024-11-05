package org.flickit.assessment.kit.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.attribute.CreateAttributeUseCase;
import org.flickit.assessment.kit.application.port.out.attribute.CreateAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.util.GenerateCodeUtil.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAttributeService implements CreateAttributeUseCase {

    private final CreateAttributePort createAttributePort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadKitVersionPort loadKitVersionPort;

    @Override
    public long createAttribute(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Attribute attribute = new Attribute(null,
            generateCode(param.getTitle()),
            param.getTitle(),
            param.getIndex(),
            param.getDescription(),
            param.getWeight(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.getCurrentUserId(),
            param.getCurrentUserId());

        return createAttributePort.persist(attribute, param.getSubjectId(), param.getKitVersionId());
    }
}
