package org.flickit.assessment.users.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupPictureUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateExpertGroupPictureService implements UpdateExpertGroupPictureUseCase {

    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;


    @Override
    public void update(Param param) {
        validateCurrentUser(param.getId(), param.getCurrentUserId());

    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
