package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.DeleteExpertGroupMemberUseCase;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.DeleteExpertGroupMemberPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteExpertGroupMemberService implements DeleteExpertGroupMemberUseCase {

    private final DeleteExpertGroupMemberPort deleteExpertGroupMemberPort;

    @Override
    public void deleteMember(Param param) {
        deleteExpertGroupMemberPort.deleteMember(param.getExpertGroupId(), param.getUserId());
    }

}
