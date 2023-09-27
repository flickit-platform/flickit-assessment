package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.Evidence;
import org.flickit.flickitassessmentcore.application.port.in.evidence.UpdateEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.GetAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.GetEvidencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.UpdateEvidencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.UPDATE_EVIDENCE_ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.UPDATE_EVIDENCE_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateEvidenceService implements UpdateEvidenceUseCase {

    private final UpdateEvidencePort updateEvidencePort;
    private final GetEvidencePort getEvidencePort;
    private final GetAssessmentPort getAssessmentPort;

    @Override
    public Result updateEvidence(Param param) {
        validateParam(param);
        var updateParam = new UpdateEvidencePort.Param(
            param.getId(),
            param.getDescription(),
            LocalDateTime.now()
        );
        return new UpdateEvidenceUseCase.Result(updateEvidencePort.update(updateParam).id());
    }

    private void validateParam(Param param) {
        Evidence evidence = getEvidencePort.getEvidenceById(param.getId())
            .orElseThrow(()-> new ResourceNotFoundException(UPDATE_EVIDENCE_ID_NOT_FOUND));
        if(getAssessmentPort.getAssessmentById(evidence.getAssessmentId()).isEmpty())
            throw new ResourceNotFoundException(UPDATE_EVIDENCE_ASSESSMENT_ID_NOT_FOUND);
    }
}
