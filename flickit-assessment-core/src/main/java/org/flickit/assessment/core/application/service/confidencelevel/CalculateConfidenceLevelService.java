package org.flickit.assessment.core.application.service.confidencelevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.confidencelevel.CalculateConfidenceLevelUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedConfidenceLevelResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CalculateConfidenceLevelService implements CalculateConfidenceLevelUseCase {

    private final LoadCalculateInfoPort loadCalculateInfoPort;
    private final UpdateCalculatedConfidenceLevelResultPort updateCalculatedConfidenceLevelResultPort;
    private final UpdateAssessmentPort updateAssessmentPort;

    @Override
    public ConfidenceLevelResult calculate(Param param) {
        AssessmentResult assessmentResult = loadCalculateInfoPort.load(param.getAssessmentId());

        double confidenceLevel = assessmentResult.calculateConfidenceLevel();

        assessmentResult.setConfidenceLevelValue(confidenceLevel);
        assessmentResult.setConfidenceValid(Boolean.TRUE);
        assessmentResult.setLastModificationTime(LocalDateTime.now());

        updateCalculatedConfidenceLevelResultPort.updateCalculatedConfidenceLevelResult(assessmentResult);

        updateAssessmentPort.updateLastModificationTime(param.getAssessmentId(), assessmentResult.getLastModificationTime());

        return new ConfidenceLevelResult(confidenceLevel);
    }
}
