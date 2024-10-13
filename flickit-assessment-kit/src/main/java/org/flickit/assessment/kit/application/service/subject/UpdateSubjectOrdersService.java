package org.flickit.assessment.kit.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectOrdersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectsIndexPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSubjectOrdersService implements UpdateSubjectOrdersUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateSubjectsIndexPort updateSubjectsIndexPort;

    @Override
    public void updateSubjectOrders(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        var ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId())) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }

        updateSubjectsIndexPort.updateIndexes(param.getKitVersionId(), param.getSubjects());
    }
}
