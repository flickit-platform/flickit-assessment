package org.flickit.assessment.users.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.users.application.domain.SpaceStatus;
import org.flickit.assessment.users.application.port.in.user.CreateUserUseCase;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
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

    @Override
    public Result createUser(Param param) {
        UUID userId = createUserPort.persist(param.getUserId(), param.getDisplayName(), param.getEmail());
        createSpacePort.persist(toParam(userId));

        return new Result(userId);
    }

    private CreateSpacePort.Param toParam(UUID userId) {
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
