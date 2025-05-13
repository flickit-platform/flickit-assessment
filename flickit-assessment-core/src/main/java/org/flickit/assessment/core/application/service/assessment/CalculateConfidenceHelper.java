package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadConfidenceLevelCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedConfidencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CalculateConfidenceHelper {

    private final LoadConfidenceLevelCalculateInfoPort loadConfidenceLevelCalculateInfoPort;
    private final UpdateCalculatedConfidencePort updateCalculatedConfidenceLevelResultPort;
    private final UpdateAssessmentPort updateAssessmentPort;

    public Double calculate(UUID assessmentId) {
        AssessmentResult assessmentResult = loadConfidenceLevelCalculateInfoPort.load(assessmentId);

        Double confidenceValue = assessmentResult.calculateConfidenceValue();

        assessmentResult.setConfidenceValue(confidenceValue);
        assessmentResult.setIsConfidenceValid(Boolean.TRUE);
        assessmentResult.setLastModificationTime(LocalDateTime.now());
        assessmentResult.setLastConfidenceCalculationTime(LocalDateTime.now());

        updateCalculatedConfidenceLevelResultPort.updateCalculatedConfidence(assessmentResult);

        updateAssessmentPort.updateLastModificationTime(assessmentId, assessmentResult.getLastModificationTime());

        return confidenceValue;
    }
}
