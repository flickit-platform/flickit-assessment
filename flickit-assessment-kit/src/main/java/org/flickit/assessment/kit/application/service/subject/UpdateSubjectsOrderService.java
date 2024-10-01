package org.flickit.assessment.kit.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitVersionExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectsIndexPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSubjectsOrderService implements UpdateSubjectsOrderUseCase {

    private final LoadKitVersionExpertGroupPort loadKitVersionExpertGroupPort;
    private final UpdateSubjectsIndexPort updateSubjectsIndexPort;

    @Override
    public void updateSubjectsOrder(Param param) {
        checkUserAccess(param.getKitVersionId(), param.getCurrentUserId());
        updateSubjectsIndexPort.updateIndexes(param.getKitVersionId(), param.getSubjectOrders());
    }

    private void checkUserAccess(Long kitVersionId, UUID currentUserId) {
        ExpertGroup expertGroup = loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(kitVersionId);
        if (!Objects.equals(currentUserId, expertGroup.getOwnerId())) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }
}
