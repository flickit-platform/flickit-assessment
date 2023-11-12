package org.flickit.assessment.core.adapter.out.persistence.qualityattribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.qualityattribute.LoadAssessmentKitAttributeModelsBySubjectPort;
import org.flickit.assessment.data.jpa.qualityattribute.QualityAttributeJpaRepository;
import org.flickit.assessment.kit.domain.Attribute;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QualityAttributePersistenceJpaAdapter implements LoadAssessmentKitAttributeModelsBySubjectPort {

    private final QualityAttributeJpaRepository repository;

    @Override
    public List<Attribute> load(Long subjectId) {
        var qualityAttributeJpaEntities = repository.findBySubjectId(subjectId);
        return qualityAttributeJpaEntities.stream()
            .map(QualityAttributeMapper::mapToKitDomainModel)
            .toList();
    }
}
