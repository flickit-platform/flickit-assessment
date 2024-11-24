package org.flickit.assessment.kit.adapter.out.persistence.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.kitversion.KitVersionJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.out.kitversion.*;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.adapter.out.persistence.kitversion.KitVersionMapper.mapKitVersionToVersionStatus;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class KitVersionPersistenceJpaAdapter implements
    LoadKitVersionPort,
    CreateKitVersionPort,
    UpdateKitVersionStatusPort,
    DeleteKitVersionPort,
    CheckKitVersionExistencePort {

    private final KitVersionJpaRepository repository;
    private final AssessmentKitJpaRepository kitRepository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public KitVersion load(long kitVersionId) {
        var entity = repository.findById(kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));
        return KitVersionMapper.mapToDomainModel(entity);
    }

    @Override
    public long persist(CreateKitVersionPort.Param param) {
        var kitEntity = kitRepository.findById(param.kitId())
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var id = sequenceGenerators.generateKitVersionId();
        var versionEntity = KitVersionMapper.createParamToJpaEntity(id, kitEntity, param);
        return repository.save(versionEntity).getId();
    }

    @Override
    public void updateStatus(long kitVersionId, KitVersionStatus newStatus) {
        long statusVersion = mapKitVersionToVersionStatus(newStatus, kitVersionId);
        repository.updateStatus(kitVersionId, newStatus.getId(), statusVersion);
    }

    @Override
    public boolean exists(long kitId, KitVersionStatus status) {
        return repository.existsByKitIdAndStatus(kitId, status.getId());
    }

    @Override
    public void delete(long kitVersionId) {
        repository.deleteById(kitVersionId);
    }
}
