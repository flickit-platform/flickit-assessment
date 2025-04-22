package org.flickit.assessment.core.adapter.out.persistence.kit.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.error.ErrorMessageKey;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Measure;
import org.flickit.assessment.core.application.port.out.measure.LoadMeasuresPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaRepository;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;

@Component("coreMeasurePersistenceJpaAdapter")
@RequiredArgsConstructor
public class MeasurePersistenceJpaAdapter implements
    LoadMeasuresPort {

    private final MeasureJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AssessmentKitJpaRepository assessmentKitRepository;

    @Override
    public List<Measure> loadAll(List<Long> measureIds, UUID assessmentId) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var language = resolveLanguage(assessmentResult);

        return repository.findAllByIdInAndKitVersionId(measureIds, assessmentResult.getKitVersionId()).stream()
            .map(entity -> MeasureMapper.mapToDomainModel(entity, language))
            .toList();
    }

    private @Nullable KitLanguage resolveLanguage(AssessmentResultJpaEntity assessmentResult) {
        var assessmentKit = assessmentKitRepository.findByKitVersionId(assessmentResult.getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageKey.COMMON_ASSESSMENT_KIT_NOT_FOUND));
        return Objects.equals(assessmentResult.getLangId(), assessmentKit.getLanguageId())
            ? null
            : KitLanguage.valueOfById(assessmentResult.getLangId());
    }
}
