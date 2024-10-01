package org.flickit.assessment.kit.adapter.out.persistence.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.kitversion.KitVersionJpaRepository;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionStatusByIdPort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class KitVersionPersistenceJpaAdapter implements
    LoadKitVersionPort,
    LoadKitVersionStatusByIdPort {

    private final KitVersionJpaRepository repository;

    @Override
    public KitVersion load(long kitVersionId) {
        var entity = repository.findById(kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));
        return KitVersionMapper.mapToDomainModel(entity);
    }

    @Override
    public KitVersionStatus loadStatusById(long id) {
        return repository.findById(id)
            .map(x -> KitVersionStatus.valueOfById(x.getStatus()))
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND));
    }
}
