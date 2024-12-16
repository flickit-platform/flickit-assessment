package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.ExportKitDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPORT_KIT_DSL_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExportKitDslService implements ExportKitDslUseCase {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadAttributesPort loadAttributesPort;

    @Override
    public Result export(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(kit.getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Long activeVersionId = kit.getActiveVersionId();
        if (activeVersionId == null)
            throw new ValidationException(EXPORT_KIT_DSL_NOT_ALLOWED);

        var attributes = loadAttributesPort.loadDslModels(activeVersionId);

        var assessmentKitDslModel = AssessmentKitDslModel.builder()
            .attributes(attributes).build();

        return new Result(assessmentKitDslModel);
    }
}
