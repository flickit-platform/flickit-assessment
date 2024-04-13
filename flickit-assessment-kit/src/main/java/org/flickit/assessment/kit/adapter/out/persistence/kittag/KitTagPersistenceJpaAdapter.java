package org.flickit.assessment.kit.adapter.out.persistence.kittag;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagJpaRepository;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.out.assessmentkittag.LoadKitTagPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KitTagPersistenceJpaAdapter implements LoadKitTagPort {

    private final KitTagJpaRepository repository;

    @Override
    public List<KitTag> load(Long assessmentKitId) {
        var tags = repository.findAllByKitId(assessmentKitId);
        return tags.stream()
            .map(KitTagMapper::toDomainModel)
            .toList();
    }
}
