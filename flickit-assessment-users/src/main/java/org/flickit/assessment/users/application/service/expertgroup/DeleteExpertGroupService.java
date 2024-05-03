package org.flickit.assessment.users.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.expertgroup.DeleteExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.CheckExpertGroupExistsPort;
import org.flickit.assessment.users.application.port.out.expertgroup.CountExpertGroupKitsPort;
import org.flickit.assessment.users.application.port.out.expertgroup.DeleteExpertGroupPort;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_EXPERT_GROUP_KITS_EXIST;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteExpertGroupService implements DeleteExpertGroupUseCase {

    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CheckExpertGroupExistsPort checkExpertGroupExistsPort;
    private final DeleteExpertGroupPort deleteExpertGroupPort;
    private final CountExpertGroupKitsPort countExpertGroupKitsPort;


    @Override
    public void deleteExpertGroup(Param param) {
        validateCurrentUser(param.getId(), param.getCurrentUserId());

        if(!checkExpertGroupExistsPort.existsById(param.getId()))
            throw new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND);

        var kitsCount = countExpertGroupKitsPort.countKits(param.getId());

        if (kitsCount.publishedKitsCount() > 0 || kitsCount.unpublishedKitsCount() > 0)
            throw new ValidationException(DELETE_EXPERT_GROUP_KITS_EXIST);

        deleteExpertGroupPort.deleteById(param.getId(), System.currentTimeMillis());
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
