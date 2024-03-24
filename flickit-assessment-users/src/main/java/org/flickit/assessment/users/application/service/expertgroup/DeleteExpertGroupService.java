package org.flickit.assessment.users.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.expertgroup.DeleteExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupExistsPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CheckKitUsedByExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.DeleteExpertGroupPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteExpertGroupService implements DeleteExpertGroupUseCase {

    private final CheckExpertGroupOwnerPort checkExpertGroupOwnerPort;
    private final CheckKitUsedByExpertGroupPort checkKitUsedByExpertGroupPort;
    private final CheckExpertGroupExistsPort checkExpertGroupExistsPort;
    private final DeleteExpertGroupPort deleteExpertGroupPort;

    @Override
    public void deleteExpertGroup(Param param) {
        boolean isOwner = checkExpertGroupOwnerPort.checkIsOwner(param.getId(), param.getCurrentUserId());
        boolean isUsed = checkKitUsedByExpertGroupPort.checkKitUsedByExpertGroupId(param.getId());
        boolean isExist = checkExpertGroupExistsPort.existsById(param.getId());

        if (!isExist)
            throw new ResourceNotFoundException(DELETE_EXPERT_GROUP_EXPERT_GROUP_ID_NOT_FOUND);

        if (!(isOwner && !isUsed))
            throw new AccessDeniedException(DELETE_EXPERT_GROUP_ACCESS_DENIED);

        deleteExpertGroupPort.deleteById(param.getId());
    }
}
