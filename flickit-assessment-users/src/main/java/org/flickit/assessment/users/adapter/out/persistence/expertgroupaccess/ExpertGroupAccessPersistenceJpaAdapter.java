package org.flickit.assessment.users.adapter.out.persistence.expertgroupaccess;

import org.flickit.assessment.users.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpertGroupAccessPersistenceJpaAdapter implements
    CreateExpertGroupAccessPort{

    private final ExpertGroupAccessJpaRepository repository;

    @Override
    public Long persist(Param param) {
        ExpertGroupAccessJpaEntity unsavedEntity = ExpertGroupAccessMapper.mapCreateParamToJpaEntity(param);
        ExpertGroupAccessJpaEntity savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }
}
