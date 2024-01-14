package org.flickit.assessment.kit.adapter.out.persistence.assessmentkitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.AssessmentKitDslJpaRepository;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateKitDslPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssessmentKitDslPersistenceJpaAdapter implements CreateKitDslPort {

    private final AssessmentKitDslJpaRepository repository;

    @Override
    public Long create(String dslFilePath, String jsonFilePath) {
        return repository.save(AssessmentKitDslMapper.toJpaEntity(dslFilePath, jsonFilePath)).getId();
    }
}
