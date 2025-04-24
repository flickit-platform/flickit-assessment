package org.flickit.assessment.core.adapter.out.persistence.kit.assessmentkit;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.port.out.assessmentkit.CheckKitAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.kitlanguage.KitLanguageJpaEntity;
import org.flickit.assessment.data.jpa.kit.kitlanguage.KitLanguageJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_KIT_ID_NOT_FOUND;

@Component("coreAssessmentKitPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentKitPersistenceJpaAdapter implements
    LoadKitLastMajorModificationTimePort,
    CheckKitAccessPort,
    LoadKitInfoPort,
    LoadAssessmentKitPort {

    private final AssessmentKitJpaRepository repository;
    private final KitLanguageJpaRepository languageRepository;

    @Override
    public LocalDateTime loadLastMajorModificationTime(Long kitId) {
        return repository.loadLastMajorModificationTime(kitId);
    }

    @Override
    public Optional<Long> checkAccess(long kitId, UUID userId) {
        return repository.existsByUserId(kitId, userId);
    }

    @Override
    public Result loadKitInfo(long id) {
        AssessmentKitJpaEntity kitEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_KIT_ID_NOT_FOUND));

        return new Result(kitEntity.getTitle(), kitEntity.getCreatedBy(), kitEntity.getExpertGroupId());
    }

    @Override
    public Optional<AssessmentKit> loadAssessmentKit(long kitId, KitLanguage language) {
        return repository.findById(kitId)
            .map(entity -> {
                var translationLanguage = resolveLanguage(entity, language);
                var kit = AssessmentKitMapper.mapToDomainModel(entity, translationLanguage);
                var languages = languageRepository.findAllByKitId(kitId).stream()
                    .map(KitLanguageJpaEntity::getLangId)
                    .map(KitLanguage::valueOfById)
                    .toList();
                kit.setSupportedLanguages(languages);
                return kit;
            });
    }

    private KitLanguage resolveLanguage(AssessmentKitJpaEntity kitEntity, KitLanguage assessmentLanguage) {
        return (assessmentLanguage != null && Objects.equals(assessmentLanguage.getId(), kitEntity.getLanguageId()))
            ? null
            : assessmentLanguage;
    }
}
