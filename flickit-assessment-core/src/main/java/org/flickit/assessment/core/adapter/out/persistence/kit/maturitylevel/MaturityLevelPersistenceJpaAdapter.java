package org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.LevelCompetence;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.out.maturitylevel.CountMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityJoinCompetenceView;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_KIT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.MATURITY_LEVEL_ID_NOT_FOUND;

@Component("coreMaturityLevelPersistenceJpaAdapter")
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements
    LoadMaturityLevelsPort,
    CountMaturityLevelsPort,
    LoadMaturityLevelPort {

    private final MaturityLevelJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AssessmentKitJpaRepository assessmentKitRepository;

    @Override
    public MaturityLevel load(long id, UUID assessmentId) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var translationLanguage = resolveLanguage(assessmentResult);

        return repository.findByIdAndKitVersionId(id, assessmentResult.getKitVersionId())
            .map(entity -> mapToDomainModel(entity, translationLanguage))
            .orElseThrow(() -> new ResourceNotFoundException(MATURITY_LEVEL_ID_NOT_FOUND));
    }

    @Override
    public List<MaturityLevel> loadByKitVersionId(Long kitVersionId) {
        return repository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(MaturityLevelMapper::mapToDomainModel)
            .toList();
    }

    public List<MaturityLevel> loadByKitVersionIdWithCompetences(Long kitVersionId) {
        var views = repository.findAllByKitVersionIdWithCompetence(kitVersionId);

        var groupedByLevelId = views.stream()
            .collect(Collectors.groupingBy(view -> view.getMaturityLevel().getId()));

        return groupedByLevelId.values().stream()
            .map(group -> {
                var levelEntity = group.getFirst().getMaturityLevel();

                var competences = group.stream()
                    .map(MaturityJoinCompetenceView::getLevelCompetence)
                    .filter(Objects::nonNull)
                    .map(MaturityLevelPersistenceJpaAdapter::mapToCompetenceDomainModel)
                    .toList();

                var level = mapToDomainModel(levelEntity);
                level.setLevelCompetences(competences);

                return level;
            })
            .toList();
    }

    private static LevelCompetence mapToCompetenceDomainModel(LevelCompetenceJpaEntity entity) {
        return new LevelCompetence(
            entity.getId(),
            entity.getValue(),
            entity.getEffectiveLevelId());
    }

    @Override
    public int count(long kitVersionId) {
        return repository.countByKitVersionId(kitVersionId);
    }

    private KitLanguage resolveLanguage(AssessmentResultJpaEntity assessmentResult) {
        var kit = assessmentKitRepository.findByKitVersionId(assessmentResult.getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_KIT_NOT_FOUND));

        return Objects.equals(assessmentResult.getLangId(), kit.getLanguageId()) ? null
            : KitLanguage.valueOfById(assessmentResult.getLangId());
    }
}
