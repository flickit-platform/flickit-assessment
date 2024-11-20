package org.flickit.assessment.kit.adapter.out.persistence.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.customkit.KitCustomJpaEntity;
import org.flickit.assessment.data.jpa.kit.customkit.KitCustomJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.port.out.kitcustom.CreateKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.UpdateKitCustomPort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_CUSTOM_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class KitCustomPersistenceJpaAdapter implements
    CreateKitCustomPort,
    UpdateKitCustomPort {

    private final KitCustomJpaRepository repository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public long persist(CreateKitCustomPort.Param param) {
        KitCustomJpaEntity entity = KitCustomMapper.mapToJpaEntity(param);
        entity.setId(sequenceGenerators.generateKitCustomId());
        return repository.save(entity).getId();
    }

    @Override
    public void update(UpdateKitCustomPort.Param param) {
        if (!repository.existsByIdAndKitId(param.id(), param.kitId()))
            throw new ResourceNotFoundException(KIT_CUSTOM_ID_NOT_FOUND);

        repository.update(param.id(),
            param.title(),
            param.code(),
            param.customData(),
            param.lastModificationTime(),
            param.lastModifiedBy()
        );
    }
}
