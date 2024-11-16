package org.flickit.assessment.kit.application.service.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answerrange.UpdateAnswerRangeUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.answerrange.UpdateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.CheckQuestionExistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_ANSWER_RANGE_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_ANSWER_RANGE_TITLE_NOT_NULL;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAnswerRangeService implements UpdateAnswerRangeUseCase {

    private final UpdateAnswerRangePort updateAnswerRangePort;
    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CheckQuestionExistencePort checkQuestionExistencePort;
    private final LoadAnswerRangePort loadAnswerRangePort;

    @Override
    public void updateAnswerRange(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var answerRange = loadAnswerRangePort.load(param.getAnswerRangeId(), param.getKitVersionId());

        if (answerRange.isReusable() && Boolean.FALSE.equals(param.getReusable()) &&
            checkQuestionExistencePort.existsByAnswerRange(param.getAnswerRangeId(), param.getKitVersionId()))
            throw new ValidationException(UPDATE_ANSWER_RANGE_NOT_ALLOWED);

        if (Boolean.TRUE.equals(param.getReusable()) && param.getTitle() == null)
            throw new ValidationException(UPDATE_ANSWER_RANGE_TITLE_NOT_NULL);

        updateAnswerRangePort.update(toParam(param));
    }

    private static UpdateAnswerRangePort.Param toParam(Param param) {
        return new UpdateAnswerRangePort.Param(param.getAnswerRangeId(),
            param.getKitVersionId(),
            param.getTitle(),
            param.getReusable(),
            LocalDateTime.now(),
            param.getCurrentUserId());
    }
}
