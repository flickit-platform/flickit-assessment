package org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute;

import org.flickit.flickitassessmentcore.domain.QualityAttribute;

public class QualityAttributeMapper {

    public static QualityAttribute toDomainModel(QualityAttributeRestAdapter.QualityAttributeDto qualityAttributeDto) {
        return new QualityAttribute(
            qualityAttributeDto.id(),
            qualityAttributeDto.weight()
        );
    }
}
