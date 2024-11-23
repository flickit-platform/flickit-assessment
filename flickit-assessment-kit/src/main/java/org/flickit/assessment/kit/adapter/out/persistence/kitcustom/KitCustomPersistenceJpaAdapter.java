package org.flickit.assessment.kit.adapter.out.persistence.kitcustom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.customkit.KitCustomJpaEntity;
import org.flickit.assessment.data.jpa.kit.customkit.KitCustomJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.domain.KitCustomData;
import org.flickit.assessment.kit.application.port.out.kitcustom.CreateKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.LoadKitCustomPort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_CUSTOM_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class KitCustomPersistenceJpaAdapter implements CreateKitCustomPort, LoadKitCustomPort {

    private final KitCustomJpaRepository repository;
    private final KitDbSequenceGenerators sequenceGenerators;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public long persist(Param param) {
        String kitCustomJson = objectMapper.writeValueAsString(param.customData());
        KitCustomJpaEntity entity = KitCustomMapper.mapToJpaEntity(param, kitCustomJson);
        entity.setId(sequenceGenerators.generateKitCustomId());
        return repository.save(entity).getId();
    }

    @Override
    @SneakyThrows
    public Result loadById(long kitCustomId) {
        var kitCustomEntity = repository.findById(kitCustomId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_CUSTOM_ID_NOT_FOUND));

        KitCustomData customData = objectMapper.readValue(kitCustomEntity.getCustomData(), KitCustomData.class);
        return new Result(kitCustomId, kitCustomEntity.getTitle(), kitCustomEntity.getKitId(), customData);
    }
}
