package org.flickit.assessment.core.adapter.out.persistence.kit.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.kit.attribute.AttributeMapper;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.flickit.assessment.core.adapter.out.persistence.kit.subject.SubjectMapper.mapToDomainModel;

@Component("coreSubjectPersistenceJpaAdapter")
@RequiredArgsConstructor
public class SubjectPersistenceJpaAdapter implements
    LoadSubjectsPort,
    LoadSubjectPort {

    private final SubjectJpaRepository repository;
    private final AttributeJpaRepository attributeRepository;

    @Override
    public List<Subject> loadByKitVersionIdWithAttributes(Long kitVersionId) {
        var views = repository.findAllByKitVersionIdOrderByIndex(kitVersionId);

        List<Long> subjectEntityIds = views.stream().map(SubjectJpaEntity::getId).toList();
        List<AttributeJpaEntity> attributeEntities = attributeRepository.findAllBySubjectIdInAndKitVersionId(subjectEntityIds, kitVersionId);
        Map<Long, List<AttributeJpaEntity>> subjectIdToAttrEntities = attributeEntities.stream()
            .collect(Collectors.groupingBy(AttributeJpaEntity::getSubjectId));

        return views.stream().map(entity -> {
            var subjectAttributeEntities = subjectIdToAttrEntities.get(entity.getId());
            List<Attribute> attributes = null;
            if (subjectAttributeEntities != null) {
                attributes = subjectAttributeEntities.stream()
                    .map(AttributeMapper::mapToDomainModel)
                    .toList();
            }

            return mapToDomainModel(entity, attributes);
        }).toList();
    }

    @Override
    public Optional<Subject> loadByIdAndKitVersionId(long id, long kitVersionId) {
        return repository.findByIdAndKitVersionId(id, kitVersionId)
            .map(entity -> mapToDomainModel(entity, null));
    }
}
