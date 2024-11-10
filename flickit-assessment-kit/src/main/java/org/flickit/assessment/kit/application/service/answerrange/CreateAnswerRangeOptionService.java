package org.flickit.assessment.kit.application.service.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeOptionUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_ANSWER_RANGE_OPTION_ANSWER_RANGE_NON_REUSABLE;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAnswerRangeOptionService implements CreateAnswerRangeOptionUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadAnswerRangePort loadAnswerRangePort;
    private final CreateAnswerOptionPort createAnswerOptionPort;

    @Override
    public Result createAnswerRangeOption(Param param) {
        checkUserAccess(param);
        AnswerRange answerRange = loadAnswerRangePort.load(param.getAnswerRangeId(), param.getKitVersionId());
        if (!answerRange.isReusable())
            throw new ValidationException(CREATE_ANSWER_RANGE_OPTION_ANSWER_RANGE_NON_REUSABLE);

        long answerOptionId = createAnswerOptionPort.persist(toCreateParam(param));
        return new CreateAnswerRangeOptionUseCase.Result(answerOptionId);
    }

    private void checkUserAccess(CreateAnswerRangeOptionUseCase.Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private CreateAnswerOptionPort.Param toCreateParam(Param param) {
        return new CreateAnswerOptionPort.Param(
            param.getTitle(),
            param.getIndex(),
            param.getAnswerRangeId(),
            param.getValue(),
            param.getKitVersionId(),
            param.getCurrentUserId());
    }
}
