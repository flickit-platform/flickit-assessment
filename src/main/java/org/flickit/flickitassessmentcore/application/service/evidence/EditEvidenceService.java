package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.Evidence;
import org.flickit.flickitassessmentcore.application.port.in.evidence.EditEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.SaveEvidencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EditEvidenceService implements EditEvidenceUseCase {

    private final LoadEvidencePort loadEvidencePort;
    private final SaveEvidencePort saveEvidencePort;

    @Override
    public Result editEvidence(Param param) {
        Evidence loadedEvidence = loadEvidencePort.loadEvidence(param.getId());
        Evidence updatedEvidence = updateEvidence(loadedEvidence, param);
        var result = saveEvidencePort.saveEvidence(updatedEvidence);
        return new Result(result);
    }

    private Evidence updateEvidence(Evidence loadedEvidence, Param param) {
        return new Evidence(
            loadedEvidence.getId(),
            param.getDescription(),
            loadedEvidence.getCreatedById(),
            loadedEvidence.getAssessmentId(),
            loadedEvidence.getQuestionId(),
            loadedEvidence.getCreationTime(),
            LocalDateTime.now()
        );
    }

}
