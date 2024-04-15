package org.flickit.assessment.users.application.service.spaceaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.spaceaccess.InviteSpaceMemberUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteSpaceMemberService implements InviteSpaceMemberUseCase {

    private final CheckSpaceExistsPort checkSpaceExistsPort;

    @Override
    public void inviteMember(Param param) {
        var currentUserId = param.getCurrentUserId();
        var space = checkSpaceExistsPort.checkById(param.getSpaceId());

    }
}
