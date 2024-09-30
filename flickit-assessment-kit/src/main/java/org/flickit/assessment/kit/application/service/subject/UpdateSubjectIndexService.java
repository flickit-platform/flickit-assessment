package org.flickit.assessment.kit.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectIndexUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitVersionExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionStatusByIdPort;
import org.flickit.assessment.kit.application.port.out.subject.DecreaseSubjectIndexPort;
import org.flickit.assessment.kit.application.port.out.subject.IncreaseSubjectIndexPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectIndexPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_NOT_UPDATING_STATUS;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSubjectIndexService implements UpdateSubjectIndexUseCase {

    private final LoadKitVersionExpertGroupPort loadKitVersionExpertGroupPort;
    private final LoadKitVersionStatusByIdPort loadKitVersionStatusByIdPort;
    private final LoadSubjectPort loadSubjectPort;
    private final UpdateSubjectIndexPort updateSubjectIndexPort;
    private final IncreaseSubjectIndexPort increaseSubjectIndexPort;
    private final DecreaseSubjectIndexPort decreaseSubjectIndexPort;

    @Override
    public void updateSubjectIndex(Param param) {
        checkUserAccess(param.getKitVersionId(), param.getCurrentUserId());
        checkKitVersionStatus(param.getKitVersionId());
        var subject = loadSubjectPort.load(param.getSubjectId(), param.getKitVersionId());

        if (!Objects.equals(subject.getIndex(), param.getIndex())) {
            updateSubjectIndexPort.updateIndex(param.getKitVersionId(), param.getSubjectId(), -1);
            if (subject.getIndex() < param.getIndex()) {
                decreaseSubjectIndexPort
                    .decreaseSubjectsIndexes(param.getKitVersionId(), subject.getIndex() + 1, param.getIndex() + 1);
            } else {
                increaseSubjectIndexPort.increaseSubjectsIndexes(param.getKitVersionId(), param.getIndex(), subject.getIndex());
            }
            updateSubjectIndexPort.updateIndex(param.getKitVersionId(), param.getSubjectId(), param.getIndex());
        }
    }

    private void checkUserAccess(Long kitVersionId, UUID currentUserId) {
        ExpertGroup expertGroup = loadKitVersionExpertGroupPort.loadKitVersionExpertGroup(kitVersionId);
        if (!Objects.equals(currentUserId, expertGroup.getOwnerId())) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }

    private void checkKitVersionStatus(Long kitVersionId) {
        KitVersionStatus status = loadKitVersionStatusByIdPort.loadStatusById(kitVersionId);
        if (!Objects.equals(status, KitVersionStatus.UPDATING)) {
            throw new ValidationException(KIT_VERSION_NOT_UPDATING_STATUS);
        }
    }
}
