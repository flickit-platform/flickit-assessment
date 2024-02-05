package org.flickit.assessment.advice.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentResultValidationFieldsPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND;

@Component("adviceAssessmentResultPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentResultPersistenceJpaAdapter implements
    LoadAssessmentResultValidationFieldsPort {

    private final AssessmentResultJpaRepository repository;

    @Override
    public Result loadValidationFields(UUID assessmentId) {
        var entity = repository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId);

        return entity.map(v -> new Result(v.getIsCalculateValid(), v.getIsConfidenceValid()))
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND));
    }
}
