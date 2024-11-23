package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.users.application.port.in.expertgroupaccess.LeaveExpertGroupUseCase;

@Service
@Transactional
@RequiredArgsConstructor
public class LeaveExpertGroupService implements LeaveExpertGroupUseCase {

    @Override
    public void leaveExpertGroup(Param param) {

    }
}
