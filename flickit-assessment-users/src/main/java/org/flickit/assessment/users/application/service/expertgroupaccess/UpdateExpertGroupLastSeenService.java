package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.UpdateExpertGroupLastSeenUseCase;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupAccessPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.UpdateExpertGroupLastSeenPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateExpertGroupLastSeenService implements UpdateExpertGroupLastSeenUseCase {

    private final LoadExpertGroupAccessPort checkExpertGroupAccessPort;
    private final UpdateExpertGroupLastSeenPort updateExpertGroupLastSeenPort;

    @Override
    public void updateLastSeen(Param param) {
        if (checkExpertGroupAccessPort.loadExpertGroupAccess(param.getExpertGroupId(), param.getCurrentUserId()).isEmpty())
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateExpertGroupLastSeenPort.updateLastSeen(param.getExpertGroupId(), param.getCurrentUserId(), LocalDateTime.now());
    }
}
