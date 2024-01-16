package org.flickit.assessment.kit.adapter.out.persistence.assessmentkittagkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkittagkit.AssessmentKitTagKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkittagkit.AssessmentKitTagKitJpaRepository;
import org.flickit.assessment.kit.application.port.out.assessmentkittag.CreateAssessmentKitTagKitPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AssessmentKitTagKitPersistenceJpaAdapter implements CreateAssessmentKitTagKitPort {

    private final AssessmentKitTagKitJpaRepository repository;

    @Override
    public void persist(List<Long> tagIds, Long kitId) {
        List<AssessmentKitTagKitJpaEntity> entities = tagIds.stream()
            .map(tagId -> new AssessmentKitTagKitJpaEntity(null, tagId, kitId))
            .toList();
        repository.saveAll(entities);
    }
}
