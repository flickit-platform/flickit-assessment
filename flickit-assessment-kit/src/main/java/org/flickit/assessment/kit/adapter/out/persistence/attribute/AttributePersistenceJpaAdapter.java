package org.flickit.assessment.kit.adapter.out.persistence.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.port.out.attribute.CreateAttributePort;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements
    UpdateAttributePort,
    CreateAttributePort {

    private final AttributeJpaRepository repository;

    @Override
    public void update(Param param) {
        repository.update(param.id(),
            param.title(),
            param.index(),
            param.description(),
            param.weight(),
            param.lastModificationTime(),
            param.subjectId());
    }

    @Override
    public Long persist(Attribute attribute, Long subjectId, Long kitId, UUID currentUserId) {
        return repository.save(AttributeMapper.mapToJpaEntity(attribute, subjectId, kitId, currentUserId)).getId();
    }
}
