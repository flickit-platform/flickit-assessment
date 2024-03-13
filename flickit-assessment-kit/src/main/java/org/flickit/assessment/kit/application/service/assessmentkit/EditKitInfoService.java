package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.EditKitInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class EditKitInfoService implements EditKitInfoUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Override
    public Result editKitInfo(Param param) {
        validateCurrentUser(param.getAssessmentKitId(), param.getCurrentUserId());
        return new Result(
            param.getTitle(),
            param.getSummary(),
            param.getIsActive(),
            param.getIsPrivate(),
            param.getPrice(),
            param.getAbout(),
            List.of()
        );
    }

    private void validateCurrentUser(Long kitId, UUID currentUserId) {
        Long expertGroupId = loadKitExpertGroupPort.loadKitExpertGroupId(kitId);
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));
        if (!Objects.equals(expertGroupOwnerId, currentUserId)) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }
}
