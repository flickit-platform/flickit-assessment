package org.flickit.assessment.kit.adapter.out.persistence.kittag;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagJpaEntity;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagJpaRepository;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagsListPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KitTagPersistenceJpaAdapter implements LoadKitTagsListPort {

    private final KitTagJpaRepository repository;

    @Override
    public List<KitTag> load(long kitId) {
        List<KitTagJpaEntity> entities = repository.findAllByKitId(kitId);
        return entities.stream()
            .map(KitTagMapper::toDomainModel)
            .toList();
    }
}
