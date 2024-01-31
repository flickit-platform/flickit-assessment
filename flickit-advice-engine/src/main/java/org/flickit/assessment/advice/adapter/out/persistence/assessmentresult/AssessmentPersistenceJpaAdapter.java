package org.flickit.assessment.advice.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentResultValidationFieldsPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.SUGGEST_ADVICE_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdapter implements
    LoadAssessmentResultValidationFieldsPort {

    private final AssessmentResultJpaRepository repository;

    @Override
    public Result loadValidationFields(UUID assessmentId) {
        var view = repository.findFirstByAssessment_IdOrderByLastModificationTimeDescValidationFields(assessmentId);

        return view.map(v -> new Result(v.getIsCalculateValid(), v.getIsConfidenceValid()))
            .orElseThrow(() -> new ResourceNotFoundException(SUGGEST_ADVICE_ASSESSMENT_RESULT_NOT_FOUND));
    }
}
