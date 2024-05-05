package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.domain.Space;
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
        LoadSpaceDetailsPort.Result result = loadSpaceDetailsPort.loadSpace(param.getId(), param.getCurrentUserId());
        updateSpaceLastSeenPort.updateLastSeen(param.getId(), LocalDateTime.now(), param.getCurrentUserId());
        Space space = new Space (result.id(), result.code(), result.title(), result.ownerId(),
            result.creationTime(), result.lastModificationTime(), result.createdBy(), result.lastModifiedBy());
        boolean isOwner = param.getCurrentUserId().equals(result.ownerId());

        return new Result(space, isOwner, result.membersCount(), result.assessmentsCount());
    }
}
