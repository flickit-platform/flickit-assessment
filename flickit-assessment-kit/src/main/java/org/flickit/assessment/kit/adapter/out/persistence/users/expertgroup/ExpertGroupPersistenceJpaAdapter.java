package org.flickit.assessment.kit.adapter.out.persistence.users.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaRepository;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupMemberIdsPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitVersionExpertGroupPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;

@Component("kitExpertGroupPersistenceJpaAdapter")
@RequiredArgsConstructor
public class ExpertGroupPersistenceJpaAdapter implements
    LoadKitExpertGroupPort,
    LoadExpertGroupOwnerPort,
    LoadExpertGroupMemberIdsPort,
    LoadKitVersionExpertGroupPort {

    private final ExpertGroupJpaRepository repository;

    @Override
    public ExpertGroup loadKitExpertGroup(Long kitId) {
        ExpertGroupJpaEntity entity = repository.findByKitId(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND));
        return ExpertGroupMapper.mapToDomainModel(entity);
    }

    @Override
    public UUID loadOwnerId(Long expertGroupId) {
        return repository.loadOwnerIdById(expertGroupId)
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));
    }

    @Override
    public List<LoadExpertGroupMemberIdsPort.Result> loadMemberIds(long expertGroupId) {
        List<UUID> memberIds = repository.findMemberIdsByExpertGroupId(expertGroupId);
        return memberIds.stream()
            .map(LoadExpertGroupMemberIdsPort.Result::new)
            .toList();
    }

    @Override
    public ExpertGroup loadKitVersionExpertGroup(Long kitVersionId) {
        return repository.findByKitVersionId(kitVersionId)
            .map(ExpertGroupMapper::mapToDomainModel)
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));
    }
}
