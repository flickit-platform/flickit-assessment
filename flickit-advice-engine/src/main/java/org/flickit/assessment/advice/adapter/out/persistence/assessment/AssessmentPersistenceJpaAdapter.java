package org.flickit.assessment.advice.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.Assessment;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentKitVersionIdPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedAttributeIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedLevelIdsRelatedToAssessmentPort;
import org.flickit.assessment.common.application.domain.ID;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND;

@Component("adviceAssessmentPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdapter implements
    LoadSelectedAttributeIdsRelatedToAssessmentPort,
    LoadSelectedLevelIdsRelatedToAssessmentPort,
    LoadAssessmentKitVersionIdPort,
    LoadAssessmentPort {

    private final AssessmentJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public Set<Long> loadSelectedAttributeIdsRelatedToAssessment(ID assessmentId, Set<Long> attributeIds) {
        return repository.findSelectedAttributeIdsRelatedToAssessment(ID.fromDomain(assessmentId), attributeIds);
    }

    @Override
    public Set<Long> loadSelectedLevelIdsRelatedToAssessment(ID assessmentId, Set<Long> levelIds) {
        return repository.findSelectedLevelIdsRelatedToAssessment(ID.fromDomain(assessmentId), levelIds);
    }

    @Override
    public Long loadKitVersionIdById(ID assessmentId) {
        return assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(ID.fromDomain(assessmentId))
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND))
            .getKitVersionId();
    }

    @Override
    public Assessment loadById(ID assessmentId) {
        return repository.findById(ID.fromDomain(assessmentId)).map(AssessmentMapper::mapToDomain)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
    }
}
