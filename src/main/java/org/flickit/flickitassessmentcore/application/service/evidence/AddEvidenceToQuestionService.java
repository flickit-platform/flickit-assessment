package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceToQuestionUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.flickit.flickitassessmentcore.domain.Evidence;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AddEvidenceToQuestionService implements AddEvidenceToQuestionUseCase {

    private final CreateEvidencePort createEvidence;

    @Override
    public Result addEvidenceToQuestion(Param param) {
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
        return new AddEvidenceToQuestionUseCase.Result(result.evidence());
    }
}
