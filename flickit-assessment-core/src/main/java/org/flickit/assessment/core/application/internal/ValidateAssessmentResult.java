package org.flickit.assessment.core.application.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ConfidenceCalculationNotValidException;
import org.flickit.assessment.common.exception.DeprecatedVersionException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.*;

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
        AssessmentKit kit = assessmentResult.getAssessment().getAssessmentKit();

        if (!Objects.equals(kit.getKitVersion(), assessmentResult.getKitVersionId())) {
            log.warn("The result's kit version is deprecated for [assessmentId={}, resultId={}, kitId={}].",
                assessmentId, assessmentResult.getId(), kit.getId());
            throw new DeprecatedVersionException(COMMON_ASSESSMENT_RESULT_KIT_VERSION_DEPRECATED);
        }

        LocalDateTime kitLastMajorModificationTime = loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(kit.getId());

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
