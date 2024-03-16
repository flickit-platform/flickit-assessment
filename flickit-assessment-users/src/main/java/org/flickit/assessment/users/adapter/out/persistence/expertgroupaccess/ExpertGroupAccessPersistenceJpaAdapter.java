package org.flickit.assessment.users.adapter.out.persistence.expertgroupaccess;

import org.flickit.assessment.users.application.port.out.expertgroupaccess.*;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExpertGroupAccessPersistenceJpaAdapter implements
    CreateExpertGroupAccessPort,
    CheckExpertGroupAccessPort,
    InviteExpertGroupMemberPort{

    private final ExpertGroupAccessJpaRepository repository;

    @Override
    public Long persist(CreateExpertGroupAccessPort.Param param) {
        ExpertGroupAccessJpaEntity unsavedEntity = ExpertGroupAccessMapper.mapCreateParamToJpaEntity(param);
        ExpertGroupAccessJpaEntity savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }

    @Override
    public boolean checkIsMember(long expertGroupId, UUID userId) {
        return repository.existsByExpertGroupIdAndUserId(expertGroupId, userId);
    }

    @Override
    public boolean persist(InviteExpertGroupMemberPort.Param param) {
        ExpertGroupAccessJpaEntity unsavedEntity = (ExpertGroupAccessMapper.mapInviteParamToJpaEntity(param));
        repository.save(unsavedEntity);
        return true;
    }
}
