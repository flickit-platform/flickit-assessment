package org.flickit.assessment.users.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.util.SlugCodeUtil;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroup.UpdateExpertGroupPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateExpertGroupService implements UpdateExpertGroupUseCase {

    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateExpertGroupPort updateExpertGroupPort;

    @Override
    public void updateExpertGroup(Param param) {
        validateCurrentUser(param.getId(), param.getCurrentUserId());
        updateExpertGroupPort.update(toParam(param));
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private UpdateExpertGroupPort.Param toParam(Param param) {
        return new UpdateExpertGroupPort.Param(
            param.getId(),
            SlugCodeUtil.generateSlugCode(param.getTitle()),
            param.getTitle(),
            param.getBio(),
            param.getAbout(),
            param.getWebsite(),
            LocalDateTime.now(),
            param.getCurrentUserId());
    }
}
