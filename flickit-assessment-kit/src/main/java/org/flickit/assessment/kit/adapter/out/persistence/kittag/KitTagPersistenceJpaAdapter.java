package org.flickit.assessment.kit.adapter.out.persistence.kittag;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagJpaEntity;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagJpaRepository;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagWithKitIdView;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitListTagsListPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagsListPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.adapter.out.persistence.kittag.KitTagMapper.toDomainModel;

@Component
@RequiredArgsConstructor
public class KitTagPersistenceJpaAdapter implements
    LoadKitTagsListPort,
    LoadKitListTagsListPort {

    private final KitTagJpaRepository repository;

    @Override
    public List<KitTag> load(long kitId) {
        List<KitTagJpaEntity> entities = repository.findAllByKitId(kitId);
        return entities.stream()
            .map(KitTagMapper::toDomainModel)
            .toList();
    }

    @Override
    public List<Result> loadByKitIds(List<Long> kitIds) {
        return repository.findAllByKitIdIn(kitIds).stream()
            .collect(Collectors.groupingBy(KitTagWithKitIdView::getKitId)).entrySet().stream()
            .map(entry -> new LoadKitListTagsListPort.Result(
                entry.getKey(),
                entry.getValue().stream()
                    .map((KitTagWithKitIdView v) -> toDomainModel(v.getKitTag()))
                    .toList()))
            .toList();
    }
}
