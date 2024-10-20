package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.assessmentkit.CloneKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CloneKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.CreateKitVersionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CloneKitService implements CloneKitUseCase {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CreateKitVersionPort createKitVersionPort;
    private final CloneKitPort cloneKitPort;

    @Override
    public long cloneKitUseCase(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        Long activeVersionId = kit.getActiveVersionId();
        long updatingVersionId = createKitVersionPort.persist(new CreateKitVersionPort.Param(kit.getId(),
            KitVersionStatus.UPDATING,
            param.getCurrentUserId()));

        cloneKitPort.cloneKit(activeVersionId, updatingVersionId, param.getCurrentUserId());

        return updatingVersionId;
    }
}
