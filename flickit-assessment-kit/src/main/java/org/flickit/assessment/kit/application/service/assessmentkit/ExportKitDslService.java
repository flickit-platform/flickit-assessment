package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.assessmentkit.ExportKitDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;


import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPORT_KIT_DSL_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ExportKitDslService implements ExportKitDslUseCase {

    LoadAssessmentKitPort loadAssessmentKitPort;
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    LoadAttributesPort loadAttributesPort;

    @Override
    public Result export(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        validateCurrentUser(kit.getExpertGroupId(), param.getCurrentUserId());

        Long activeVersionId = kit.getActiveVersionId();
        if (activeVersionId == null)
            throw new ValidationException(EXPORT_KIT_DSL_NOT_ALLOWED);

        var attributes = loadAttributesPort.loadDslModels(activeVersionId);

        return null;
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
