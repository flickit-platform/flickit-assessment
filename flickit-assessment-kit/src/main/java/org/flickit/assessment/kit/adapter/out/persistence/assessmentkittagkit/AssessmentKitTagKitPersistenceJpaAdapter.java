package org.flickit.assessment.kit.adapter.out.persistence.assessmentkittagkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkittagkit.AssessmentKitTagKitJpaRepository;
import org.flickit.assessment.kit.application.port.out.assessmentkittag.CreateAssessmentKitTagKitPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssessmentKitTagKitPersistenceJpaAdapter implements CreateAssessmentKitTagKitPort {

    private final AssessmentKitTagKitJpaRepository repository;

    @Override
    public Long persist(Param param) {
        return repository.save(AssessmentKitTagKitMapper.toJpaEntity(param)).getId();
    }
}
