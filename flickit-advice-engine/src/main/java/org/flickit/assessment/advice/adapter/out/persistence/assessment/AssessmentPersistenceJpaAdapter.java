package org.flickit.assessment.advice.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.Assessment;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentKitLanguagePort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentKitVersionIdPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedAttributeIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedLevelIdsRelatedToAssessmentPort;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
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
    LoadAssessmentPort,
    LoadAssessmentKitLanguagePort,
    LoadAssessmentKitVersionIdPort {

    private final AssessmentJpaRepository repository;

    @Override
    public Set<Long> loadSelectedAttributeIdsRelatedToAssessment(UUID assessmentId, Set<Long> attributeIds) {
        return repository.findSelectedAttributeIdsRelatedToAssessment(assessmentId, attributeIds);
    }

    @Override
    public Set<Long> loadSelectedLevelIdsRelatedToAssessment(UUID assessmentId, Set<Long> levelIds) {
        return repository.findSelectedLevelIdsRelatedToAssessment(assessmentId, levelIds);
    }

    @Override
    public Assessment loadById(UUID assessmentId) {
        return repository.findById(assessmentId).map(AssessmentMapper::mapToDomain)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
    }

    @Override
    public KitLanguage loadKitLanguage(UUID assessmentId) {
        int languageId = repository.loadKitLanguageByAssessmentId(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
        return KitLanguage.valueOfById(languageId);
    }
}
