package org.flickit.assessment.kit.adapter.out.persistence.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements UpdateAttributePort {

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
}
