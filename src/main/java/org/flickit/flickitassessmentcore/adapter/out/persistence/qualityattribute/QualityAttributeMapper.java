package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattribute;

import org.flickit.flickitassessmentcore.adapter.out.persistence.question.QuestionPersistenceJpaAdapter;
import org.flickit.flickitassessmentcore.domain.QualityAttribute;

public class QualityAttributeMapper {
    public static QualityAttribute toDomainModel(QuestionPersistenceJpaAdapter.QualityAttributeDto dto) {
        return new QualityAttribute(
            dto.id(),
            dto.code(),
            dto.title(),
            dto.description(),
            null,
            null,
            null,
            dto.index(),
            null
        );
    }
}
