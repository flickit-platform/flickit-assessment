package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.in.space.UpdateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSpaceService implements UpdateSpaceUseCase {

    private final LoadSpacePort loadSpacePort;

    @Override
    public void updateSpace(Param param) {
        Space space = loadSpacePort.loadSpace(param.getId());
        if (!param.getCurrentUserId().equals(space.getOwnerId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);


    }
}
