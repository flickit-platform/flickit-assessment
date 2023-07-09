package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattribute;

import org.flickit.flickitassessmentcore.adapter.out.persistence.question.QuestionPersistenceJpaAdapter;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubjectPort;
import org.flickit.flickitassessmentcore.domain.QualityAttribute;

import java.util.List;
import java.util.stream.Collectors;

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

    public static LoadQualityAttributeBySubjectPort.Result toResult(List<QualityAttributePersistenceJpaAdapter.QualityAttributeDto> items) {
        return new LoadQualityAttributeBySubjectPort.Result(
            items.stream().
                map(QualityAttributeMapper::toDomainModel).
                collect(Collectors.toList())
        );
    }

    public static QualityAttribute toDomainModel(QualityAttributePersistenceJpaAdapter.QualityAttributeDto qualityAttributeDto) {
        return new QualityAttribute(
            qualityAttributeDto.id(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            qualityAttributeDto.weight()
        );
    }
}
