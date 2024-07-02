package org.flickit.assessment.advice.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentKitVersionIdPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedAttributeIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedLevelIdsRelatedToAssessmentPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND;

@Component("adviceAssessmentPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdapter implements
    LoadSelectedAttributeIdsRelatedToAssessmentPort,
    LoadSelectedLevelIdsRelatedToAssessmentPort,
    LoadAssessmentKitVersionIdPort {

    private final AssessmentJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public Set<Long> loadSelectedAttributeIdsRelatedToAssessment(UUID assessmentId, Set<Long> attributeIds) {
        return repository.findSelectedAttributeIdsRelatedToAssessment(assessmentId, attributeIds);
    }

    @Override
    public Set<Long> loadSelectedLevelIdsRelatedToAssessment(UUID assessmentId, Set<Long> levelIds) {
        return repository.findSelectedLevelIdsRelatedToAssessment(assessmentId, levelIds);
    }

    @Override
    public Long loadKitVersionIdById(UUID assessmentId) {
        return assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND))
            .getKitVersionId();
    }
}
