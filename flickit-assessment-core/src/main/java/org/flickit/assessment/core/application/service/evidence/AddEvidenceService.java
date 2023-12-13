package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.assessment.core.application.port.out.evidence.CreateEvidencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.ADD_EVIDENCE_ASSESSMENT_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class AddEvidenceService implements AddEvidenceUseCase {

    private final CreateEvidencePort createEvidencePort;
    private final CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Override
    public Result addEvidence(Param param) {
        if (!checkAssessmentExistencePort.existsById(param.getAssessmentId()))
            throw new ResourceNotFoundException(ADD_EVIDENCE_ASSESSMENT_ID_NOT_FOUND);
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
            param.getQuestionId()
        );
    }
}
