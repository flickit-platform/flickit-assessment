package org.flickit.assessment.kit.application.service.answerrange;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAnswerRangeService implements CreateAnswerRangeUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CreateAnswerRangePort createAnswerRangePort;

    @Override
    public Result createAnswerRange(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        var expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!expertGroupOwnerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return new Result(createAnswerRangePort.persist(toCreateAnswerRangePortParam(param)));
    }

    private CreateAnswerRangePort.Param toCreateAnswerRangePortParam(Param param) {
        return new CreateAnswerRangePort.Param(param.getKitVersionId(),
            param.getTitle(),
            Boolean.TRUE,
            param.getCurrentUserId());
    }
}
