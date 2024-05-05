package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.in.space.UpdateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.users.application.port.out.space.UpdateSpacePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSpaceService implements UpdateSpaceUseCase {

    private final LoadSpacePort loadSpacePort;
    private final UpdateSpacePort updateSpacePort;

    @Override
    public void updateSpace(Param param) {
        Space space = loadSpacePort.loadSpace(param.getId());
        if (!param.getCurrentUserId().equals(space.getOwnerId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateSpacePort.updateSpace(toParam(param.getId(), param.getTitle(),
                LocalDateTime.now(), param.getCurrentUserId()));
    }

    private UpdateSpacePort.Param toParam(Long id, String title, LocalDateTime now, UUID currentUserId) {
        return new UpdateSpacePort.Param(id, title, now, currentUserId);
    }
}
