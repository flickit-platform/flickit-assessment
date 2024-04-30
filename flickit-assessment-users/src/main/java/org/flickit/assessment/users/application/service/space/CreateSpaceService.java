package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.users.application.port.in.space.CreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.AddSpaceMemberPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateSpaceService implements CreateSpaceUseCase {

    private final CreateSpacePort createSpacePort;
    private final AddSpaceMemberPort addSpaceMemberPort;

    @Override
    public Result createSpace(Param param) {
        long id = createSpacePort.persist(toCreateParam(param.getTitle(), param.getCurrentUserId()));
        addSpaceMemberPort.persist(toAddMemberParam(id,param.getCurrentUserId(),param.getCurrentUserId()));
        return new Result(id);
    }

    CreateSpacePort.Param toCreateParam(String title, UUID currentUserId) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new CreateSpacePort.Param(
            RandomStringUtils.randomAlphabetic(10),
            title,
            currentUserId,
            creationTime,
            creationTime,
            currentUserId,
            currentUserId
        );
    }

    AddSpaceMemberPort.Param toAddMemberParam(long id, UUID invitee, UUID inviter){
        return new AddSpaceMemberPort.Param(id, invitee, inviter, LocalDateTime.now());
    }
}
