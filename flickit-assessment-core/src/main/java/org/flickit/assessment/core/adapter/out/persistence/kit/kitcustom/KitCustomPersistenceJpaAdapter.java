package org.flickit.assessment.core.adapter.out.persistence.kit.kitcustom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.kitcustom.LoadKitCustomPort;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaRepository;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.core.common.ErrorMessageKey.KIT_CUSTOM_ID_NOT_FOUND;

@Component("coreKitCustomPersistenceJpaAdapter")
@RequiredArgsConstructor
public class KitCustomPersistenceJpaAdapter implements LoadKitCustomPort {

    private final KitCustomJpaRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public KitCustomData loadCustomDataByIdAndKitId(long kitCustomId, long kitId) {
        var kitCustomEntity = repository.findByIdAndKitId(kitCustomId, kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_CUSTOM_ID_NOT_FOUND));

        return objectMapper.readValue(kitCustomEntity.getCustomData(), KitCustomData.class);
    }
}
