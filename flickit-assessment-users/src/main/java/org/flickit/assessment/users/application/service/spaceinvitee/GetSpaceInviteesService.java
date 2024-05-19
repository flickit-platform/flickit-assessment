package org.flickit.assessment.users.application.service.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.SpaceInvitee;
import org.flickit.assessment.users.application.port.in.spaceinvitee.GetSpaceInviteesUseCase;
import org.flickit.assessment.users.application.port.out.space.CheckSpaceExistsPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.LoadSpaceInviteesPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSpaceInviteesService implements GetSpaceInviteesUseCase {

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final LoadSpaceInviteesPort loadSpaceMembersPort;
    private final CheckSpaceExistsPort checkSpaceExistsPort;

    @Override
    public PaginatedResponse<Invitee> getInvitees(Param param) {
        if (!checkSpaceExistsPort.existById(param.getId()))
            throw new ResourceNotFoundException(SPACE_ID_NOT_FOUND);

        if (!checkSpaceAccessPort.checkIsMember(param.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var portResult = loadSpaceMembersPort.loadInvitees(param.getId(), param.getPage(), param.getSize());
        var invitees = mapToInvitees(portResult.getItems());

        return new PaginatedResponse<>(
            invitees,
            portResult.getPage(),
            portResult.getSize(),
            portResult.getSort(),
            portResult.getOrder(),
            portResult.getTotal()
        );
    }

    private List<Invitee> mapToInvitees(List<SpaceInvitee> items) {
        return items.stream()
            .map(item -> new GetSpaceInviteesUseCase.Invitee(
                item.getId(),
                item.getEmail(),
                item.getExpirationTime(),
                item.getInviteTime(),
                item.getInviterId()))
            .toList();
    }
}
