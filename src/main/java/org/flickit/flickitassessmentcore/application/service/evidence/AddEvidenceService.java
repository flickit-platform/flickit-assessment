package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AddEvidenceService implements AddEvidenceUseCase {

    private final CreateEvidencePort createEvidencePort;

    @Override
    public Result addEvidence(Param param) {
        var createPortParam = toCreatePortParam(param);
        UUID id = createEvidencePort.persist(createPortParam);
        return new AddEvidenceUseCase.Result(id);
    }

    private CreateEvidencePort.Param toCreatePortParam(AddEvidenceUseCase.Param param) {
        return new CreateEvidencePort.Param(
            param.getDescription(),
            param.getCreatedById(),
            param.getAssessmentId(),
            param.getQuestionId()
        );
    }
}
