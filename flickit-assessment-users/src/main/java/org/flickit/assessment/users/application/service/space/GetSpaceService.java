package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.space.GetSpaceUseCase;
import org.flickit.assessment.users.application.port.out.LoadSpaceDetailsPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.UpdateSpaceLastSeenPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class GetSpaceService implements GetSpaceUseCase {

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final LoadSpaceDetailsPort loadSpaceDetailsPort;
    private final UpdateSpaceLastSeenPort updateSpaceLastSeenPort;

    @Override
    public Result getSpace(Param param) {
        if (!checkSpaceAccessPort.checkIsMember(param.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        LoadSpaceDetailsPort.Result spaceDetails = loadSpaceDetailsPort.loadSpace(param.getId());

        updateSpaceLastSeenPort.updateLastSeen(param.getId(), param.getCurrentUserId(), LocalDateTime.now());

        boolean editable = param.getCurrentUserId().equals(spaceDetails.space().getOwnerId());

        return new Result(spaceDetails.space(), editable, spaceDetails.membersCount(), spaceDetails.assessmentsCount());
    }
}
