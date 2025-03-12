package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.notification.SendNotification;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.domain.notification.CreatePremiumSpaceNotificationCmd;
import org.flickit.assessment.users.application.port.in.space.CreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CountSpacesPort;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.users.common.ErrorMessageKey.CREATE_SPACE_BASIC_SPACE_MAX;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateSpaceService implements CreateSpaceUseCase {

    private final CreateSpacePort createSpacePort;
    private final CreateSpaceUserAccessPort createSpaceUserAccessPort;
    private final CountSpacesPort countSpacesPort;
    private final AppSpecProperties appSpecProperties;

    @Override
    @SendNotification
    public Result createSpace(Param param) {
        var space = mapToDomain(param);
        var maxBasicSpaces = appSpecProperties.getSpace().getMaxBasicSpaces();
        boolean isBasicSpaceLimitReached = SpaceType.BASIC.getCode().equals(param.getType())
            && countSpacesPort.countBasicSpaces(param.getCurrentUserId()) >= maxBasicSpaces;

        if (isBasicSpaceLimitReached)
            throw new UpgradeRequiredException(CREATE_SPACE_BASIC_SPACE_MAX);

        long id = createSpacePort.persist(space);
        createOwnerAccessToSpace(id, param.getCurrentUserId(), param.getCurrentUserId());

        if (SpaceType.PREMIUM == SpaceType.valueOf(param.getType())) {
            String adminEmail = appSpecProperties.getEmail().getAdminEmail();
            return new CreatePremium(id, new CreatePremiumSpaceNotificationCmd(adminEmail, space));
        }

        return new CreateBasic(id);
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
