package org.flickit.assessment.kit.adapter.out.persistence.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessJpaRepository;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpertGroupAccessPersistenceJpaAdapter implements
    CreateExpertGroupAccessPort {

    private final ExpertGroupAccessJpaRepository repository;

    @Override
    public Long persist(Param param) {
        ExpertGroupAccessJpaEntity unsavedEntity = ExpertGroupAccessMapper.mapCreateParamToJpaEntity(param);
        ExpertGroupAccessJpaEntity savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }
}
