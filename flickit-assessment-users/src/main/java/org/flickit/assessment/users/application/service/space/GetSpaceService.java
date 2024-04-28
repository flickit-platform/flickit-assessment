package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.in.space.GetSpaceUseCase;
import org.flickit.assessment.users.application.port.out.LoadSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.UpdateSpaceLastSeenPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class GetSpaceService implements GetSpaceUseCase {

    private final LoadSpacePort loadSpacePort;
    private final UpdateSpaceLastSeenPort updateSpaceLastSeenPort;

    @Override
    public Result getSpace(Param param) {
        Space space = loadSpacePort.loadSpace(param.getId(), param.getCurrentUserId());
        updateSpaceLastSeenPort.updateLastSeen(param.getId(), LocalDateTime.now(), param.getCurrentUserId());

        return new Result(space.getId(),
            space.getCode(),
            space.getTitle(),
            space.getOwnerId().equals(param.getCurrentUserId()),
            space.getLastModificationTime(),
            space.getMembersCount(),
            space.getAssessmentsCount());
    }
}
