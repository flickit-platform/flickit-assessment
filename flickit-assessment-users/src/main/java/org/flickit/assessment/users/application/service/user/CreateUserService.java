package org.flickit.assessment.users.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.application.domain.space.SpaceStatus;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.user.CreateUserUseCase;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.user.CreateUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.users.application.service.constant.SpaceConstants.DEFAULT_SPACE_TITLE;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final CreateUserPort createUserPort;
    private final CreateSpacePort createSpacePort;
    private final CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Override
    public Result createUser(Param param) {
        UUID userId = createUserPort.persist(toCreateUserPortParam(param.getUserId(), param.getDisplayName(), param.getEmail()));

        createDefaultSpace(userId);

        return new Result(userId);
    }

    private CreateUserPort.Param toCreateUserPortParam(UUID userId, String displayName, String email) {
        var currentTime = LocalDateTime.now();
        return new CreateUserPort.Param(userId,
            displayName,
            email,
            currentTime,
            currentTime);
    }

    private void createDefaultSpace(UUID userId) {
        var spaceId = createSpacePort.persist(toCreateSpacePortParam(userId));
        var spaceUserAccess = new SpaceUserAccess(spaceId, userId, userId, LocalDateTime.now());
        createSpaceUserAccessPort.persist(spaceUserAccess);
    }

    private CreateSpacePort.Param toCreateSpacePortParam(UUID userId) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new CreateSpacePort.Param(
            generateSlugCode(DEFAULT_SPACE_TITLE),
            DEFAULT_SPACE_TITLE,
            SpaceType.BASIC,
            SpaceStatus.ACTIVE,
            null,
            true,
            userId,
            creationTime);
    }
}
