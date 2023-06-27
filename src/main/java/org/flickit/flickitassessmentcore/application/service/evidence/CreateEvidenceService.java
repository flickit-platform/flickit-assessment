package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.CreateEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.flickit.flickitassessmentcore.domain.Evidence;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateEvidenceService implements CreateEvidenceUseCase {

    private final CreateEvidencePort createEvidence;

    @Override
    public Result createEvidence(Param param) {
        Evidence evidence = new Evidence(
            null,
            param.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.getCreatedById(),
            param.getAssessmentId(),
            param.getQuestionId()
        );
        CreateEvidencePort.Result result = createEvidence.createEvidence(new CreateEvidencePort.Param(evidence));
        return new CreateEvidenceUseCase.Result(result.evidence());
    }
}
