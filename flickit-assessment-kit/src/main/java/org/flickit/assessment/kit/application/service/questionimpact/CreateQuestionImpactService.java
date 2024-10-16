package org.flickit.assessment.kit.application.service.questionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.port.in.questionimpact.CreateQuestionImpactUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateQuestionImpactService implements CreateQuestionImpactUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CreateQuestionImpactPort createQuestionImpactPort;

    @Override
    public long createQuestionImpact(Param param) {
        var kitversion = loadKitVersionPort.load(param.getKitVersionId());
        var expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(kitversion.getKit().getExpertGroupId());

        if (!expertGroupOwnerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return createQuestionImpactPort.persist(toQuestionImpact(param));
    }

    private QuestionImpact toQuestionImpact(Param param) {
        return new QuestionImpact(null,
            param.getAttributeId(),
            param.getMaturityLevelId(),
            param.getWeight(),
            param.getKitVersionId(),
            param.getQuestionId(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.getCurrentUserId(),
            param.getCurrentUserId());
    }
}
