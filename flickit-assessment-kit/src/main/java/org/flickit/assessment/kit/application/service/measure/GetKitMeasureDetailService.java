package org.flickit.assessment.kit.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.measure.GetKitMeasureDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadKitQuestionnaireDetailPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitMeasureDetailService implements GetKitMeasureDetailUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final LoadQuestionnairesPort loadQuestionnairesPort;
    private final LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadMeasurePort loadMeasurePort;

    @Override
    public Result getKitMeasureDetail(Param param) {
        return null;

    }
}
