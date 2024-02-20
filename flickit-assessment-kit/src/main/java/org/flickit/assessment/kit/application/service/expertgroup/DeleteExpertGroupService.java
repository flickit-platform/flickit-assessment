package org.flickit.assessment.kit.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupUsedByKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.DeleteExpertGroupPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteExpertGroupService {

    private final DeleteExpertGroupPort deleteExpertGroupPort;
    private final CheckExpertGroupOwnerPort checkExpertGroupOwnerPort;
    private final CheckExpertGroupUsedByKitPort checkExpertGroupUsedByKitPort;

    final void deleteExpertGroup(long expertGroupId, UUID currentUserId) {
        boolean isOwner = checkExpertGroupOwnerPort.checkIsOwner(expertGroupId, currentUserId);
        boolean isUsed = checkExpertGroupUsedByKitPort.checkByKitId(expertGroupId);

        if (isOwner && !isUsed)
            deleteExpertGroupPort.deleteById(expertGroupId);
        else
            throw new AccessDeniedException(DELETE_EXPERT_GROUP_ACCESS_DENIED);
    }
}
