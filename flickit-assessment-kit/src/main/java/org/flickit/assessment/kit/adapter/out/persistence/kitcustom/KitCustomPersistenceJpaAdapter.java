package org.flickit.assessment.kit.adapter.out.persistence.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.customkit.KitCustomJpaEntity;
import org.flickit.assessment.data.jpa.kit.customkit.KitCustomJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.port.out.kitcustom.CreateKitCustomPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KitCustomPersistenceJpaAdapter implements CreateKitCustomPort {

    private final KitCustomJpaRepository repository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public long persist(Param param) {
        KitCustomJpaEntity entity = KitCustomMapper.mapToJpaEntity(param);
        entity.setId(sequenceGenerators.generateKitCustomId());
        return repository.save(entity).getId();
    }
}
