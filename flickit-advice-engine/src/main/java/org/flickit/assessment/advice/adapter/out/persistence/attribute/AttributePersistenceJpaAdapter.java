package org.flickit.assessment.advice.adapter.out.persistence.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.Attribute;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.advice.adapter.out.persistence.attribute.AttributeMapper.mapToDomainModel;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_KIT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;

@Component("adviceAttributePersistenceJpaAdapter")
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements LoadAttributesPort {

    private final AttributeJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AssessmentKitJpaRepository assessmentKitRepository;

    @Override
    public List<Attribute> loadByIdsAndAssessmentId(List<Long> attributeIds, UUID assessmentId) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        var translationLanguage = resolveLanguage(assessmentResult.getKitVersionId(), assessmentResult.getLangId());
        return repository.findAllByIdInAndKitVersionId(attributeIds, assessmentResult.getKitVersionId()).stream()
            .map(entity -> mapToDomainModel(entity, translationLanguage))
            .toList();
    }

    private KitLanguage resolveLanguage(long kitVersionId, int langId) {
        var assessmentKit = assessmentKitRepository.findByKitVersionId(kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_KIT_NOT_FOUND));
        return Objects.equals(langId, assessmentKit.getLanguageId())
            ? null
            : KitLanguage.valueOfById(langId);
    }
}
