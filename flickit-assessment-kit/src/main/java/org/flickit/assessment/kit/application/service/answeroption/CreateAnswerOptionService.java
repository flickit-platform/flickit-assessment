package org.flickit.assessment.kit.application.service.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answeroption.CreateAnswerOptionUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
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
    private final CreateAnswerOptionPort createAnswerOptionPort;

    @Override
    public Result createAnswerOption(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Long answerOptionId = createAnswerOptionPort.persist(toCreateParam(param));
        return new Result(answerOptionId);
    }

    private CreateAnswerOptionPort.Param toCreateParam(Param param) {
        return new CreateAnswerOptionPort.Param(
            param.getTitle(),
            param.getIndex(),
            param.getQuestionId(),
            param.getKitVersionId(),
            param.getCurrentUserId());
    }
}
