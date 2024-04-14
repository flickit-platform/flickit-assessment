package org.flickit.assessment.kit.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetKitQuestionnaireDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CheckKitExistByIdPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.CheckQuestionnaireExistByIdAndKitIdPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.CheckQuestionnaireExistByIdPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadKitQuestionnaireDetailPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitQuestionnaireDetailService implements GetKitQuestionnaireDetailUseCase {

    private final LoadKitQuestionnaireDetailPort port;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckKitExistByIdPort checkKitExistByIdPort;
    private final CheckQuestionnaireExistByIdPort checkQuestionnaireExistByIdPort;
    private final CheckQuestionnaireExistByIdAndKitIdPort checkQuestionnaireExistByIdAndKitIdPort;

    @Override
    public Result getKitQuestionnaireDetail(Param param) {
        if (!checkKitExistByIdPort.checkKitExistByIdPort(param.getKitId()))
            throw new ResourceNotFoundException(KIT_ID_NOT_FOUND);

        ExpertGroup expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }

        if (!checkQuestionnaireExistByIdPort.checkQuestionnaireExistById(param.getQuestionnaireId()))
            throw new ResourceNotFoundException(QUESTIONNAIRE_ID_NOT_FOUND);

        if (!checkQuestionnaireExistByIdAndKitIdPort.checkQuestionnaireExistByIdAndKitId(param.getQuestionnaireId(), param.getKitId()))
            throw new ResourceNotFoundException(QUESTIONNAIRE_QUESTIONNAIRE_ID_KIT_ID_NOT_FOUND);

        var kitQuestionnaireDetail = port.loadKitQuestionnaireDetail(param.getQuestionnaireId(), param.getKitId());

        return new Result(kitQuestionnaireDetail.questionsCount(),
            kitQuestionnaireDetail.relatedSubjects(),
            kitQuestionnaireDetail.description(),
            kitQuestionnaireDetail.questions());
    }
}
