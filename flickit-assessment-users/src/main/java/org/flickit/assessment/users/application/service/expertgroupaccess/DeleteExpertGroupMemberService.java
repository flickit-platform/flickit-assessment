package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.DeleteExpertGroupMemberUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.DeleteExpertGroupMemberPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_EXPERT_GROUP_MEMBER_USER_ID_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteExpertGroupMemberService implements DeleteExpertGroupMemberUseCase {

    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadExpertGroupAccessPort loadExpertGroupAccessPort;
    private final DeleteExpertGroupMemberPort deleteExpertGroupMemberPort;

    @Override
    public void deleteMember(Param param) {
        validateCurrentUser(param.getExpertGroupId(), param.getCurrentUserId());
        var access = loadExpertGroupAccessPort.loadExpertGroupAccess(param.getExpertGroupId(), param.getUserId());
        if(access.isEmpty())
            throw new ResourceNotFoundException(DELETE_EXPERT_GROUP_MEMBER_USER_ID_NOT_FOUND);
        deleteExpertGroupMemberPort.deleteMember(param.getExpertGroupId(), param.getUserId());
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
