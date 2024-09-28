package org.flickit.assessment.advice.adapter.out.persistence.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.Attribute;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("adviceAttributePersistenceJpaAdapter")
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements LoadAttributesPort {

    private final AttributeJpaRepository repository;

    @Override
    public List<Attribute> loadByIdsAndKitVersionId(List<Long> attributeIds, long kitVersionId) {
        return repository.findAllByIdInAndKitVersionId(attributeIds, kitVersionId).stream()
            .map(AttributeMapper::mapToDomainModel)
            .toList();
    }
}
