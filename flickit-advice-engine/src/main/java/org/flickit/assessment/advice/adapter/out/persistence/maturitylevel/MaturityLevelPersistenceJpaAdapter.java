package org.flickit.assessment.advice.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.error.ErrorMessageKey;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component("adviceMaturityLevelPersistenceJpaAdapter")
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements LoadMaturityLevelsPort {

    private final MaturityLevelJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AssessmentKitJpaRepository assessmentKitRepository;

    @Override
    public List<MaturityLevel> loadAll(UUID assessmentId) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var translationLanguage = resolveLanguage(assessmentResult);

        return repository.findAllByKitVersionIdOrderByIndex(assessmentResult.getKitVersionId()).stream()
            .map(entity -> MaturityLevelMapper.mapToDomainModel(entity, translationLanguage))
            .toList();
    }

    private KitLanguage resolveLanguage(AssessmentResultJpaEntity assessmentResult) {
        var assessmentKit = assessmentKitRepository.findByKitVersionId(assessmentResult.getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageKey.COMMON_ASSESSMENT_KIT_NOT_FOUND));
        return Objects.equals(assessmentResult.getLangId(), assessmentKit.getLanguageId())
            ? null
            : KitLanguage.valueOfById(assessmentResult.getLangId());
    }
}
