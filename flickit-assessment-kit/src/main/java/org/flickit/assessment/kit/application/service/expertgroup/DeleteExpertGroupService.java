package org.flickit.assessment.kit.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupExistsPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CheckKitUsedByExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.DeleteExpertGroupPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteExpertGroupService {

    private final CheckExpertGroupOwnerPort checkExpertGroupOwnerPort;
    private final CheckKitUsedByExpertGroupPort checkKitUsedByExpertGroupPort;
    private final CheckExpertGroupExistsPort checkExpertGroupExistsPort;
    private final DeleteExpertGroupPort deleteExpertGroupPort;

    final void deleteExpertGroup(long expertGroupId, UUID currentUserId) {
        boolean isOwner = checkExpertGroupOwnerPort.checkIsOwner(expertGroupId, currentUserId);
        boolean isUsed = checkKitUsedByExpertGroupPort.checkKitUsedByExpertGroupId(expertGroupId);
        boolean isExist = checkExpertGroupExistsPort.existsById(expertGroupId);

        if (!isExist)
            throw new ResourceNotFoundException(DELETE_EXPERT_GROUP_EXPERT_GROUP_ID_NOT_FOUND);

        if (isOwner && !isUsed)
            deleteExpertGroupPort.deleteById(expertGroupId);
        else
            throw new AccessDeniedException(DELETE_EXPERT_GROUP_ACCESS_DENIED);
    }
}
