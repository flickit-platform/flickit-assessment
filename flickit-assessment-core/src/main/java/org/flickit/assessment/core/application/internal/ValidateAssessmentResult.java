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
import org.flickit.assessment.core.application.port.out.kitcustom.LoadKitCustomLastModificationTimePort;
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
    private final LoadKitCustomLastModificationTimePort loadKitCustomLastModificationTimePort;

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

        if (!Boolean.TRUE.equals(assessmentResult.getIsCalculateValid())) {
            log.warn("The calculated result is not valid for [assessmentId={}, resultId={}].", assessmentId, assessmentResult.getId());
            throw new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID);
        }

        if (!Boolean.TRUE.equals(assessmentResult.getIsConfidenceValid())) {
            log.warn("The calculated confidence value is not valid for [assessmentId={}, resultId={}].", assessmentId, assessmentResult.getId());
            throw new ConfidenceCalculationNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID);
        }

        LocalDateTime kitLastMajorModificationTime = loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(kit.getId());
        validateCalculationTime(assessmentResult, kitLastMajorModificationTime);

        if (assessmentResult.getAssessment().getKitCustomId() != null) {
            var kitCustomId = assessmentResult.getAssessment().getKitCustomId();
            var kitCustomLastUpdate = loadKitCustomLastModificationTimePort.loadLastModificationTime(kitCustomId);
            validateCalculationTime(assessmentResult, kitCustomLastUpdate);
        }
    }

    private void validateCalculationTime(AssessmentResult assessmentResult, LocalDateTime modificationTime) {
        var calculationTime = assessmentResult.getLastCalculationTime();
        if (calculationTime == null || calculationTime.isBefore(modificationTime)) {
            log.warn("The calculated result is not valid for [assessmentId={}, resultId={}].", assessmentResult.getAssessment().getId(), assessmentResult.getId());
            throw new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID);
        }

        var confCalculationTime = assessmentResult.getLastConfidenceCalculationTime();
        if (confCalculationTime == null || confCalculationTime.isBefore(modificationTime)) {
            log.warn("The calculated confidence value is not valid for [assessmentId={}, resultId={}].", assessmentResult.getAssessment().getId(), assessmentResult.getId());
            throw new ConfidenceCalculationNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID);
        }
    }
}
