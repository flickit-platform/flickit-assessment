package org.flickit.assessment.kit.adapter.out.persistence.qualityattribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.qualityattribute.QualityAttributeJpaRepository;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.port.out.qualityattribute.LoadQualityAttributeByCodePort;
import org.flickit.assessment.kit.application.port.out.qualityattribute.LoadQualityAttributePort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QualityAttributePersistenceJpaAdapter implements
    LoadQualityAttributePort,
    LoadQualityAttributeByCodePort {

    private final QualityAttributeJpaRepository repository;

    @Override
    public Optional<Attribute> load(Long id) {
        var entity = repository.findById(id);
        return entity.map(QualityAttributeMapper::mapToDomain);
    }

    @Override
    public Attribute loadByCode(String code) {
        return QualityAttributeMapper.mapToDomain(repository.findByCode(code));
    }
}
