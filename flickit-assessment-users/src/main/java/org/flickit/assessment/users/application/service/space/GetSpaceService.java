package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.space.GetSpaceUseCase;
import org.flickit.assessment.users.application.port.out.LoadSpaceDetailsPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.UpdateSpaceLastSeenPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class GetSpaceService implements GetSpaceUseCase {

    private final LoadSpaceDetailsPort loadSpaceDetailsPort;
    private final UpdateSpaceLastSeenPort updateSpaceLastSeenPort;

    @Override
    public Result getSpace(Param param) {
        LoadSpaceDetailsPort.Result spaceDetails = loadSpaceDetailsPort.loadSpace(param.getId(), param.getCurrentUserId());

        updateSpaceLastSeenPort.updateLastSeen(param.getId(), param.getCurrentUserId(), LocalDateTime.now());

        boolean editable = param.getCurrentUserId().equals(spaceDetails.space().getOwnerId());

        return new Result(spaceDetails.space(), editable, spaceDetails.membersCount(), spaceDetails.assessmentsCount());
    }
}
