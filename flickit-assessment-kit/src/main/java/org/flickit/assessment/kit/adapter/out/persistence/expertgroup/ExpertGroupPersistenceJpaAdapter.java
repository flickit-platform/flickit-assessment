package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupList;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExpertGroupPersistenceJpaAdapter implements
    LoadExpertGroupOwnerPort,
    LoadExpertGroupList {

    private final ExpertGroupJpaRepository repository;

    @Override
    public Optional<UUID> loadOwnerId(Long expertGroupId) {
        return Optional.of(repository.loadOwnerIdById(expertGroupId));
    }

    @Override
    public Optional<UUID> loadExpertGroupList() {
        return Optional.empty();
    }
}
