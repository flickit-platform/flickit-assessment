package org.flickit.assessment.kit.application.service.answeroptions;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AnswerOptionOrder;
import org.flickit.assessment.kit.application.port.in.answeroptions.UpdateAnswerOptionOrdersUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAnswerOptionOrdersService implements UpdateAnswerOptionOrdersUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateAnswerOptionPort updateAnswerOptionPort;

    @Override
    public void changeOrders(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());

        var expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());

        if (!expertGroupOwnerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var answerOptionOrders = param.getOrders().stream().map(this::toAnswerOptionOrder).toList();
        updateAnswerOptionPort.updateOrders(answerOptionOrders, param.getKitVersionId(), param.getCurrentUserId());
    }

    private AnswerOptionOrder toAnswerOptionOrder(AnswerOptionParam param) {
        return new AnswerOptionOrder(param.getId(), param.getIndex());
    }
}
