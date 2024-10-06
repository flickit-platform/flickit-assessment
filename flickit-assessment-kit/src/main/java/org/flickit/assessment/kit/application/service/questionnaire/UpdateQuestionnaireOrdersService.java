package org.flickit.assessment.kit.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.QuestionnaireOrder;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireOrdersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateQuestionnaireOrdersService implements UpdateQuestionnaireOrdersUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateQuestionnairePort updateQuestionnairePort;

    @Override
    public void changeOrders(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        var ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());

        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var questionnairesOrders = param.getOrders().stream().map(this::toQuestionnaireOrderParam).toList();
        updateQuestionnairePort.updateOrders(questionnairesOrders, param.getKitVersionId(), param.getCurrentUserId());
    }

    private QuestionnaireOrder toQuestionnaireOrderParam(QuestionnaireParam param) {
        return new QuestionnaireOrder(param.getId(), param.getIndex());
    }
}
