package org.flickit.assessment.kit.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.in.question.UpdateQuestionsOrderUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateQuestionsOrderService implements UpdateQuestionsOrderUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateQuestionPort updateQuestionPort;

    @Override
    public void updateQuestionsOrder(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());

        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateQuestionPort.updateOrders(toParam(param.getKitVersionId(),
            param.getOrders(),
            param.getQuestionnaireId(),
            param.getCurrentUserId()));
    }

    private UpdateQuestionPort.UpdateOrderParam toParam(long kitVersionId,
                                                        List<Param.QuestionOrder> orders,
                                                        long questionnaireId,
                                                        UUID currentUserId) {
        var outPortOrders = orders.stream()
            .map(e -> new UpdateQuestionPort.UpdateOrderParam.QuestionOrder(e.getQuestionId(),
                e.getIndex(),
                Question.generateCode(e.getIndex())))
            .toList();

        return new UpdateQuestionPort.UpdateOrderParam(kitVersionId,
            outPortOrders,
            questionnaireId,
            LocalDateTime.now(),
            currentUserId);
    }
}
