package org.flickit.assessment.core.application.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ConfidenceCalculationNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_VALID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidateAssessmentResult implements ValidateAssessmentResultPort {

    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadKitLastMajorModificationTimePort loadKitLastMajorModificationTimePort;

    @Override
    public void validate(UUID assessmentId) {
        AssessmentResult assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        long kitId = assessmentResult.getAssessment().getAssessmentKit().getId();
        LocalDateTime kitLastMajorModificationTime = loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(kitId);

        if (!Boolean.TRUE.equals(assessmentResult.getIsCalculateValid()) ||
            !isCalculationTimeValid(assessmentResult.getLastCalculationTime(), kitLastMajorModificationTime)) {
            log.warn("The calculated result is not valid for [assessmentId={}, resultId={}].", assessmentId, assessmentResult.getId());
            throw new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID);
        }

        if (!Boolean.TRUE.equals(assessmentResult.getIsConfidenceValid()) ||
            !isCalculationTimeValid(assessmentResult.getLastConfidenceCalculationTime(), kitLastMajorModificationTime)) {
            log.warn("The calculated confidence value is not valid for [assessmentId={}, resultId={}].", assessmentId, assessmentResult.getId());
            throw new ConfidenceCalculationNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID);
        }
    }

    private boolean isCalculationTimeValid(LocalDateTime calculationTime, LocalDateTime kitLastMajorModificationTime) {
        return calculationTime != null &&
            calculationTime.isAfter(kitLastMajorModificationTime);
    }
}
