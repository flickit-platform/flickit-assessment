package org.flickit.assessment.kit.adapter.out.persistence.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.port.out.attribute.CheckAttributeExistByAttributeIdAndKitIdPort;
import org.flickit.assessment.kit.application.port.out.attribute.CreateAttributePort;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.adapter.out.persistence.attribute.AttributeMapper.mapToJpaEntity;

@Component
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements
    UpdateAttributePort,
    CreateAttributePort,
    CheckAttributeExistByAttributeIdAndKitIdPort {

    private final AttributeJpaRepository repository;
    private final SubjectJpaRepository subjectRepository;

    @Override
    public void update(UpdateAttributePort.Param param) {
        repository.update(param.id(),
            param.title(),
            param.index(),
            param.description(),
            param.weight(),
            param.lastModificationTime(),
            param.lastModifiedBy(),
            param.subjectId());
    }

    @Override
    public Long persist(Attribute attribute, Long subjectId, Long kitVersionId) {
        SubjectJpaEntity subjectJpaEntity = subjectRepository.getReferenceById(subjectId);
        return repository.save(mapToJpaEntity(attribute, kitVersionId, subjectJpaEntity)).getId();
    }

    @Override
    public boolean checkAttrExistsByAttrIdAndKitId(Long attributeId, Long kitId) {
        return repository.existByAttributeIdAndKitId(attributeId, kitId);
    }
}
