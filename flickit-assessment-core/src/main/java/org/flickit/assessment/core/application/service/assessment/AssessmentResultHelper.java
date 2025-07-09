package org.flickit.assessment.core.application.service.assessment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.kitcustom.LoadKitCustomLastModificationTimePort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AssessmentResultHelper {

    private final LoadKitLastMajorModificationTimePort loadKitLastMajorModificationTimePort;
    private final LoadKitCustomLastModificationTimePort loadKitCustomLastModificationTimePort;
    private final CalculateAssessmentHelper calculateAssessmentHelper;
    private final CalculateConfidenceHelper calculateConfidenceHelper;

    public void recalculateAssessmentResultIfRequired(AssessmentResult assessmentResult) {
        var calculateValid = Boolean.TRUE.equals(assessmentResult.getIsCalculateValid());
        var kitLastMajorModificationTime = loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(
            assessmentResult.getAssessment().getAssessmentKit().getId());
        var calculationTimeValid = validateCalculationTime(assessmentResult, kitLastMajorModificationTime);
        var customKitUpdateTimeValid = true;
        if (assessmentResult.getAssessment().getKitCustomId() != null) {
            var kitCustomId = assessmentResult.getAssessment().getKitCustomId();
            var kitCustomLastUpdate = loadKitCustomLastModificationTimePort.loadLastModificationTime(kitCustomId);
            customKitUpdateTimeValid = validateCalculationTime(assessmentResult, kitCustomLastUpdate);
        }
        var isCalculationValid = calculateValid && calculationTimeValid && customKitUpdateTimeValid;

        var confidenceValid = Boolean.TRUE.equals(assessmentResult.getIsConfidenceValid());
        var confidenceTimeValid = confidenceValidateCalculationTime(assessmentResult, kitLastMajorModificationTime);
        var isConfidenceValid = confidenceValid && confidenceTimeValid;

        var assessmentId = assessmentResult.getAssessment().getId();
        var assessmentResultId = assessmentResult.getId();

        if (!isCalculationValid) {
            log.info("Recalculate assessment for resultId=[{}] of assessmentId=[{}] due to invalid calculation.", assessmentResultId, assessmentId);
            calculateAssessmentHelper.calculateMaturityLevel(assessmentResult, kitLastMajorModificationTime);
        }

        if (!isConfidenceValid) {
            log.info("Recalculate confidence for resultId=[{}] of assessmentId=[{}] due to invalid confidence value.", assessmentResultId, assessmentId);
            calculateConfidenceHelper.calculate(assessmentResult, kitLastMajorModificationTime);
        }
    }

    private boolean validateCalculationTime(AssessmentResult assessmentResult, LocalDateTime modificationTime) {
        var calculationTime = assessmentResult.getLastCalculationTime();
        return calculationTime != null && !calculationTime.isBefore(modificationTime);
    }

    private boolean confidenceValidateCalculationTime(AssessmentResult assessmentResult, LocalDateTime modificationTime) {
        var confCalculationTime = assessmentResult.getLastConfidenceCalculationTime();
        return confCalculationTime != null && !confCalculationTime.isBefore(modificationTime);
    }
}
