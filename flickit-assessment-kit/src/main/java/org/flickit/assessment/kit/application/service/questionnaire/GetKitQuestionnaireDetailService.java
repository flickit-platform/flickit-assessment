package org.flickit.assessment.kit.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetKitQuestionnaireDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadKitQuestionnaireDetailPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitQuestionnaireDetailService implements GetKitQuestionnaireDetailUseCase {

    private final LoadKitQuestionnaireDetailPort port;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Override
    public Result getKitQuestionnaireDetail(Param param) {
        Long expertGroupId = loadKitExpertGroupPort.loadKitExpertGroupId(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroupId, param.getCurrentUserId())) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
        var kitQuestionnaireDetail = port.loadKitQuestionnaireDetail(param.getQuestionnaireId(), param.getKitId());

        return new Result(kitQuestionnaireDetail.questionsCount(),
            kitQuestionnaireDetail.relatedSubjects(),
            kitQuestionnaireDetail.description(),
            kitQuestionnaireDetail.questions());
    }
}
