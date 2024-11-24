package org.flickit.assessment.kit.adapter.out.persistence.kitcustom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaEntity;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.domain.KitCustomData;
import org.flickit.assessment.kit.application.port.out.kitcustom.CreateKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.LoadKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.UpdateKitCustomPort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_CUSTOM_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class KitCustomPersistenceJpaAdapter implements
    CreateKitCustomPort,
    LoadKitCustomPort,
    UpdateKitCustomPort {

    private final KitCustomJpaRepository repository;
    private final KitDbSequenceGenerators sequenceGenerators;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public long persist(CreateKitCustomPort.Param param) {
        String kitCustomJson = objectMapper.writeValueAsString(param.customData());
        KitCustomJpaEntity entity = KitCustomMapper.mapToJpaEntity(param, kitCustomJson);
        entity.setId(sequenceGenerators.generateKitCustomId());
        return repository.save(entity).getId();
    }

    @Override
    @SneakyThrows
    public LoadKitCustomPort.Result loadByIdAndKitId(long kitCustomId, long kitId) {
        var kitCustomEntity = repository.findByIdAndKitId(kitCustomId, kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_CUSTOM_ID_NOT_FOUND));

        KitCustomData customData = objectMapper.readValue(kitCustomEntity.getCustomData(), KitCustomData.class);
        return new LoadKitCustomPort.Result(kitCustomId, kitCustomEntity.getTitle(), kitCustomEntity.getKitId(), customData);
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
