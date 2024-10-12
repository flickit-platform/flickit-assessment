package org.flickit.assessment.kit.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.question.UpdateQuestionsOrderUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionsOrderPort;
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
    private final UpdateQuestionsOrderPort updateQuestionsOrderPort;

    @Override
    public void updateQuestionsOrder(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());

        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateQuestionsOrderPort.updateQuestionsOrder(toParam(param.getKitVersionId(),
            param.getOrders(),
            param.getCurrentUserId()));
    }

    private UpdateQuestionsOrderPort.Param toParam(long kitVersionId, List<Param.QuestionOrder> orders, UUID currentUserId) {
        var outPortOrders = orders.stream()
            .map(e -> new UpdateQuestionsOrderPort.Param.QuestionOrder(e.getQuestionId(), e.getIndex()))
            .toList();
        return new UpdateQuestionsOrderPort.Param(kitVersionId, outPortOrders, LocalDateTime.now(), currentUserId);
    }
}
