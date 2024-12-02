package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.DeleteExpertGroupMemberPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.users.application.port.in.expertgroupaccess.LeaveExpertGroupUseCase;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.LEAVE_EXPERT_GROUP_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class LeaveExpertGroupService implements LeaveExpertGroupUseCase {

    private final LoadExpertGroupAccessPort loadExpertGroupAccessPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final DeleteExpertGroupMemberPort deleteExpertGroupMemberPort;

    @Override
    public void leaveExpertGroup(Param param) {
        if (loadExpertGroupAccessPort.loadExpertGroupAccess(param.getExpertGroupId(), param.getCurrentUserId()).isEmpty())
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId()).equals(param.getCurrentUserId()))
            throw new ValidationException(LEAVE_EXPERT_GROUP_NOT_ALLOWED);

        deleteExpertGroupMemberPort.deleteMember(param.getExpertGroupId(), param.getCurrentUserId());
    }
}
