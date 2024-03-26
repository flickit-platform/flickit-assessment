package org.flickit.assessment.kit.adapter.out.persistence.users.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.kit.application.port.out.expertgroup.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component("kitExpertGroupPersistenceJpaAdapter")
@RequiredArgsConstructor
public class ExpertGroupPersistenceJpaAdapter implements
    LoadExpertGroupOwnerPort,
    LoadExpertGroupMemberIdsPort{

    private final ExpertGroupJpaRepository repository;

    @Override
    public Optional<UUID> loadOwnerId(Long expertGroupId) {
        return Optional.of(repository.loadOwnerIdById(expertGroupId));
    }

    @Override
    public List<LoadExpertGroupMemberIdsPort.Result> loadMemberIds(long expertGroupId) {
        List<UUID> memberIds = repository.findMemberIdsByExpertGroupId(expertGroupId);
        return memberIds.stream()
            .map(LoadExpertGroupMemberIdsPort.Result::new)
            .toList();
    }
}
