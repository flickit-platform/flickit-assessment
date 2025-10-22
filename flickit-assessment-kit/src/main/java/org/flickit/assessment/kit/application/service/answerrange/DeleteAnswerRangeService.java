package org.flickit.assessment.kit.application.service.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answerrange.DeleteAnswerRangeUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.DeleteAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.DeleteQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAnswerRangeService implements DeleteAnswerRangeUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final DeleteAnswerRangePort deleteAnswerRangePort;
    private final DeleteQuestionPort deleteQuestionPort;

    @Override
    public void deleteAnswerRange(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        deleteAnswerRangePort.delete(param.getAnswerRangeId(), kitVersion.getId());
        deleteQuestionPort.deleteQuestionAnswerRange(toParam(param.getAnswerRangeId(), param.getKitVersionId(), param.getCurrentUserId()));
    }

    DeleteQuestionPort.Param toParam(long answerRangeId, long kitVersionId, UUID lastModifiedBy) {
        return new DeleteQuestionPort.Param(answerRangeId, kitVersionId, LocalDateTime.now(), lastModifiedBy);
    }
}
