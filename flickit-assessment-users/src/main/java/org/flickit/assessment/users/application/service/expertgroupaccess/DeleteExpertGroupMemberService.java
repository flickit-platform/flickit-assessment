package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.DeleteExpertGroupMemberUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.DeleteExpertGroupMemberPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_EXPERT_GROUP_MEMBER_USER_ID_OWNER_DELETION_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteExpertGroupMemberService implements DeleteExpertGroupMemberUseCase {

    private final LoadExpertGroupPort loadExpertGroupPort;
    private final DeleteExpertGroupMemberPort deleteExpertGroupMemberPort;

    @Override
    public void deleteMember(Param param) {
        ExpertGroup expertGroup = loadExpertGroupPort.loadExpertGroup(param.getExpertGroupId());
        validateCurrentUser(param.getCurrentUserId(), expertGroup);
        if (Objects.equals(expertGroup.getOwnerId(), param.getUserId()))
            throw new ValidationException(DELETE_EXPERT_GROUP_MEMBER_USER_ID_OWNER_DELETION_NOT_ALLOWED);

        deleteExpertGroupMemberPort.deleteMember(param.getExpertGroupId(), param.getUserId());
    }

    private void validateCurrentUser(UUID currentUserId, ExpertGroup expertGroup) {
        if (!Objects.equals(expertGroup.getOwnerId(), currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
