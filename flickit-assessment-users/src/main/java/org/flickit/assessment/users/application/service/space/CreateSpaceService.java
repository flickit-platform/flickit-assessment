package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.notification.SendNotification;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.domain.notification.CreatePremiumSpaceNotificationCmd;
import org.flickit.assessment.users.application.port.in.space.CreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateSpaceService implements CreateSpaceUseCase {

    private final CreateSpacePort createSpacePort;
    private final CreateSpaceUserAccessPort createSpaceUserAccessPort;
    private final AppSpecProperties appSpecProperties;

    @Override
    @SendNotification
    public Result createSpace(Param param) {
        var space = mapToDomain(param);
        long id = createSpacePort.persist(space);

        createOwnerAccessToSpace(id, param.getCurrentUserId(), param.getCurrentUserId());

        String adminEmail = appSpecProperties.getEmail().getAdminEmail();
        return new Result(id, new CreatePremiumSpaceNotificationCmd(adminEmail, space));
    }

    private Space mapToDomain(Param param) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new Space(null,
            generateSlugCode(param.getTitle()),
            param.getTitle(),
            SpaceType.valueOf(param.getType()),
            param.getCurrentUserId(),
            null,
            creationTime,
            creationTime,
            param.getCurrentUserId(),
            param.getCurrentUserId()
        );
    }

    private void createOwnerAccessToSpace(long id, UUID invitee, UUID inviter) {
        createSpaceUserAccessPort.persist(new SpaceUserAccess(id, invitee, inviter, LocalDateTime.now()));
    }
}
