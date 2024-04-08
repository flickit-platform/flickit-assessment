package org.flickit.assessment.kit.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetQuestionnaireService implements GetQuestionnaireUseCase {

    private final LoadQuestionnairePort port;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Override
    public Result getQuestionnaire(Param param) {
        Long expertGroupId = loadKitExpertGroupPort.loadKitExpertGroupId(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroupId, param.getCurrentUserId())) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
        return port.loadQuestionnaire(param.getQuestionnaireId(), param.getKitId());
    }
}
