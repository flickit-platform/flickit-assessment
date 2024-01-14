package org.flickit.assessment.kit.adapter.out.persistence.kitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaRepository;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateKitDslPort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.adapter.out.persistence.kitdsl.KitDslMapper.toJpaEntity;

@Component
@RequiredArgsConstructor
public class KitDslPersistenceJpaAdapter implements CreateKitDslPort {

    private final KitDslJpaRepository repository;

    @Override
    public Long create(String dslFilePath, String jsonFilePath) {
        return repository.save(toJpaEntity(dslFilePath, jsonFilePath)).getId();
    }
}
