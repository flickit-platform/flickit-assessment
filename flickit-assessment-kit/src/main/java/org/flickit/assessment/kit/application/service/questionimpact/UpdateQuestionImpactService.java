package org.flickit.assessment.kit.application.service.questionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.questionimpact.UpdateQuestionImpactUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateQuestionImpactService implements UpdateQuestionImpactUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateQuestionImpactPort updateQuestionImpactPort;

    @Override
    public void updateQuestionImpact(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateQuestionImpactPort.update(toParam(param.getQuestionImpactId(),
            param.getKitVersionId(),
            param.getWeight(),
            param.getAttributeId(),
            param.getMaturityLevelId(),
            param.getCurrentUserId()));
    }

    private UpdateQuestionImpactPort.Param toParam(long kitVersionId,
                                                   long questionImpactId,
                                                   int weight,
                                                   long attributeId,
                                                   long maturityLevelId,
                                                   UUID currentUserId) {
        return new UpdateQuestionImpactPort.Param(kitVersionId,
            questionImpactId,
            weight,
            attributeId,
            maturityLevelId,
            LocalDateTime.now(),
            currentUserId);
    }
}
