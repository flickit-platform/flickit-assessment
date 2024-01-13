package org.flickit.assessment.kit.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.expertgroup.UpdateExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.UpdateExpertGroupPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateExpertGroupService implements
    UpdateExpertGroupUseCase {

    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateExpertGroupPort updateExpertGroupPort;

    @Override
    public void updateExpertGroup(Param param) {
        validateCurrentUser(param.getId(), param.getCurrentUserId());
        updateExpertGroupPort.update(toExpertGroupParam(param));
        log.debug("User [{}] access to Expert Group [{}] denied.", param.getCurrentUserId(), param.getId());
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));
        if (!Objects.equals(expertGroupOwnerId, currentUserId)) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }

    private UpdateExpertGroupPort.Param toExpertGroupParam(Param param) {
        return new UpdateExpertGroupPort.Param(
            param.getId(),
            param.getName(),
            param.getAbout(),
            param.getPicture(),
            param.getWebsite(),
            param.getBio(),
            param.getCurrentUserId()
        );
    }
}
