package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedConfidencePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadConfidenceLevelCalculateInfoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CalculateConfidenceService implements CalculateConfidenceUseCase {

    private final LoadConfidenceLevelCalculateInfoPort loadConfidenceLevelCalculateInfoPort;
    private final UpdateCalculatedConfidencePort updateCalculatedConfidenceLevelResultPort;
    private final UpdateAssessmentPort updateAssessmentPort;

    @Override
    public Result calculate(Param param) {
        AssessmentResult assessmentResult = loadConfidenceLevelCalculateInfoPort.load(param.getAssessmentId());

        Double confidenceValue = assessmentResult.calculateConfidenceValue();

        assessmentResult.setConfidenceValue(confidenceValue);
        assessmentResult.setConfidenceValid(Boolean.TRUE);
        assessmentResult.setLastModificationTime(LocalDateTime.now());

        updateCalculatedConfidenceLevelResultPort.updateCalculatedConfidence(assessmentResult);

        updateAssessmentPort.updateLastModificationTime(param.getAssessmentId(), assessmentResult.getLastModificationTime());

        return new Result(confidenceValue);
    }
}
