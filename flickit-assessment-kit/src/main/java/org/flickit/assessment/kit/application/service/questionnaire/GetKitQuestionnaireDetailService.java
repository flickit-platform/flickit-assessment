package org.flickit.assessment.kit.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetKitQuestionnaireDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadKitQuestionnaireDetailPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitQuestionnaireDetailService implements GetKitQuestionnaireDetailUseCase {

    private final LoadKitQuestionnaireDetailPort loadKitQuestionnaireDetailPort;
    private final LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Override
    public Result getKitQuestionnaireDetail(Param param) {
        ExpertGroup expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var kitVersionId = loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId());
        var kitQuestionnaireDetail = loadKitQuestionnaireDetailPort.loadKitQuestionnaireDetail(param.getQuestionnaireId(), kitVersionId);

        return new Result(kitQuestionnaireDetail.questionsCount(),
            kitQuestionnaireDetail.relatedSubjects(),
            kitQuestionnaireDetail.description(),
            kitQuestionnaireDetail.questions());
    }
}
