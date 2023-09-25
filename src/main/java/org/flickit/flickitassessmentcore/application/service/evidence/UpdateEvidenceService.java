package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.UpdateEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CheckAssessmentExistenceByEvidenceIdPort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.UpdateEvidencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.UPDATE_EVIDENCE_ASSESSMENT_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateEvidenceService implements UpdateEvidenceUseCase {

    private final UpdateEvidencePort updateEvidencePort;
    private final CheckAssessmentExistenceByEvidenceIdPort checkAssessmentExistencePort;

    @Override
    public Result updateEvidence(Param param) {
        if (!checkAssessmentExistencePort.isAssessmentExistsByEvidenceId(param.getId()))
            throw new ResourceNotFoundException(UPDATE_EVIDENCE_ASSESSMENT_ID_NOT_FOUND);
        var updateParam = new UpdateEvidencePort.Param(
            param.getId(),
            param.getDescription(),
            LocalDateTime.now()
        );
        return new UpdateEvidenceUseCase.Result(updateEvidencePort.update(updateParam).id());
    }

}
