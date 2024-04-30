package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.GetSpaceMembersUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSpaceMembersService implements GetSpaceMembersUseCase {

    private final CheckSpaceAccessPort checkSpaceAccessPort;

    @Override
    public PaginatedResponse<Member> getSpaceMembers(Param param) {
        if (!checkSpaceAccessPort.checkIsMember(param.getId(), param.getCurrentUserId()))
            return new PaginatedResponse<>(List.of(), 0, 0, null, null, 0);

        return null;
    }
}
