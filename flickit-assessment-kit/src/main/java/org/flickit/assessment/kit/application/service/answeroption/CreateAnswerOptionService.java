package org.flickit.assessment.kit.application.service.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answeroption.CreateAnswerOptionUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;


@Service
@Transactional
@RequiredArgsConstructor
public class CreateAnswerOptionService implements CreateAnswerOptionUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CreateAnswerRangePort createAnswerRangePort;
    private final CreateAnswerOptionPort createAnswerOptionPort;

    @Override
    public Result createAnswerOption(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Long answerRangeId = param.getAnswerRangeId();
        if (answerRangeId == null) {
            answerRangeId = createAnswerRangePort.persist(tocreateAnswerRangePortParam(param));
        }
        long answerOptionId = createAnswerOptionPort.persist(toCreateParam(param, answerRangeId));
        return new Result(answerOptionId, answerRangeId);
    }

    private CreateAnswerRangePort.Param tocreateAnswerRangePortParam(Param param) {
        return new CreateAnswerRangePort.Param(param.getKitVersionId(), null, false, param.getCurrentUserId());
    }

    private CreateAnswerOptionPort.Param toCreateParam(Param param, Long answerRangeId) {
        return new CreateAnswerOptionPort.Param(
            param.getTitle(),
            param.getIndex(),
            answerRangeId,
            param.getValue(),
            param.getKitVersionId(),
            param.getCurrentUserId());
    }
}
