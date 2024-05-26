package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.EvidenceType;
import org.flickit.assessment.core.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.evidence.CreateEvidencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class AddEvidenceService implements AddEvidenceUseCase {

    private final CreateEvidencePort createEvidencePort;
    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Override
    public Result addEvidence(Param param) {
        if (!checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCreatedById()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var createPortParam = toCreatePortParam(param);
        UUID id = createEvidencePort.persist(createPortParam);
        return new AddEvidenceUseCase.Result(id);
    }

    private CreateEvidencePort.Param toCreatePortParam(AddEvidenceUseCase.Param param) {
        return new CreateEvidencePort.Param(
            param.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.getCreatedById(),
            param.getAssessmentId(),
            param.getQuestionId(),
            param.getType() != null ? EvidenceType.valueOf(param.getType()).ordinal() : null
        );
    }
}
