package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.EditEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.SaveEvidencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.Evidence;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.EDIT_EVIDENCE_EVIDENCE_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class EditEvidenceService implements EditEvidenceUseCase {

    private final LoadEvidencePort loadEvidence;
    private final SaveEvidencePort saveEvidence;

    @Override
    public Result editEvidence(Param param) {
        Evidence loadedEvidence = loadEvidence.loadEvidence(new LoadEvidencePort.Param(param.getId())).evidence();
        if (loadedEvidence == null) {
            throw new ResourceNotFoundException(EDIT_EVIDENCE_EVIDENCE_NOT_FOUND);
        }
        Evidence updatedEvidence = updateEvidence(loadedEvidence, param);
        SaveEvidencePort.Result result = saveEvidence.saveEvidence(new SaveEvidencePort.Param(updatedEvidence));
        return new Result(result.id());
    }

    private Evidence updateEvidence(Evidence loadedEvidence, Param param) {
        return new Evidence(
            loadedEvidence.getId(),
            param.getDescription(),
            loadedEvidence.getCreationTime(),
            LocalDateTime.now(),
            loadedEvidence.getCreatedById(),
            loadedEvidence.getAssessmentId(),
            loadedEvidence.getQuestionId()
        );
    }

}
