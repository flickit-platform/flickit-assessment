package org.flickit.assessment.core.adapter.out.persistence.kit.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.kitcustom.LoadKitCustomLastModificationTimePort;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("coreKitCustomPersistenceJpaAdapter")
@RequiredArgsConstructor
public class KitCustomPersistenceJpaAdapter implements LoadKitCustomLastModificationTimePort {

    private final KitCustomJpaRepository repository;

    @Override
    public LocalDateTime loadLastModificationTime(long kitCustomId) {
        return repository.loadLastModificationTime(kitCustomId);
    }
}
