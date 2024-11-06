package org.flickit.assessment.kit.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectOrdersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSubjectOrdersService implements UpdateSubjectOrdersUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateSubjectPort updateSubjectPort;

    @Override
    public void updateSubjectOrders(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        var ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateSubjectPort.updateOrders(toUpdatePortParam(param));
    }

    private UpdateSubjectPort.UpdateOrderParam toUpdatePortParam(Param param) {
        var subjectOrders = param.getSubjects().stream()
            .map(e -> new UpdateSubjectPort.UpdateOrderParam.SubjectOrder(e.getId(), e.getIndex()))
            .toList();
        return new UpdateSubjectPort.UpdateOrderParam(subjectOrders, param.getKitVersionId(), LocalDateTime.now(), param.getCurrentUserId());
    }
}
