package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.assessmentkit.CloneKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CloneKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.CreateKitVersionPort;
import org.flickit.assessment.kit.application.port.out.kitversion.CheckKitVersionExistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CLONE_KIT_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CloneKitService implements CloneKitUseCase {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CreateKitVersionPort createKitVersionPort;
    private final CheckKitVersionExistencePort checkKitVersionExistencePort;
    private final CloneKitPort cloneKitPort;

    @Override
    public long cloneKitUseCase(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        Long activeVersionId = kit.getActiveVersionId();

        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (checkKitVersionExistencePort.exists(kit.getId(), KitVersionStatus.UPDATING)) {
            log.warn("KitVersion with kitId {} and updating status already exists", kit.getId());
            throw new ValidationException(CLONE_KIT_NOT_ALLOWED);
        }

        long updatingVersionId = createKitVersionPort.persist(toCreateKitVersionPortParam(param));
        cloneKitPort.cloneKit(toCloneKitPortParam(activeVersionId, updatingVersionId, param.getCurrentUserId()));
        return updatingVersionId;
    }

    private CreateKitVersionPort.Param toCreateKitVersionPortParam(Param param) {
        return new CreateKitVersionPort.Param(param.getKitId(),
            KitVersionStatus.UPDATING,
            param.getCurrentUserId());
    }

    private CloneKitPort.Param toCloneKitPortParam(Long activeVersionId, Long updatingVersionId, UUID clonedBy) {
        return new CloneKitPort.Param(activeVersionId, updatingVersionId, clonedBy, LocalDateTime.now());
    }
}
