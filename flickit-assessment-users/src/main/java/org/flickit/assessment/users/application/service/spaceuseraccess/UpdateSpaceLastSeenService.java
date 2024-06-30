package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.UpdateSpaceLastSeenUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.UpdateSpaceLastSeenPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSpaceLastSeenService implements UpdateSpaceLastSeenUseCase {

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final UpdateSpaceLastSeenPort updateSpaceLastSeenPort;

    @Override
    public void updateLastSeen(Param param) {
        if (!checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateSpaceLastSeenPort.updateLastSeen(param.getSpaceId(), param.getCurrentUserId(), LocalDateTime.now());
    }
}
