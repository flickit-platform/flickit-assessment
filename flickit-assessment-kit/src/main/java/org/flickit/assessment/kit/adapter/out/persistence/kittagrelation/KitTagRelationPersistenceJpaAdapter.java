package org.flickit.assessment.kit.adapter.out.persistence.kittagrelation;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kittagrelation.KitTagRelationJpaEntity;
import org.flickit.assessment.data.jpa.kit.kittagrelation.KitTagRelationJpaRepository;
import org.flickit.assessment.kit.application.port.out.assessmentkittag.CreateKitTagRelationPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KitTagRelationPersistenceJpaAdapter implements CreateKitTagRelationPort {

    private final KitTagRelationJpaRepository repository;

    @Override
    public void persist(List<Long> tagIds, Long kitId) {
        List<KitTagRelationJpaEntity> entities = tagIds.stream()
            .map(tagId -> new KitTagRelationJpaEntity(tagId, kitId))
            .toList();
        repository.saveAll(entities);
    }
}
