package org.flickit.assessment.kit.adapter.out.persistence.kittag;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagJpaEntity;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagJpaRepository;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagListPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagsLisByKitIdtPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KitTagPersistenceJpaAdapter implements LoadKitTagsLisByKitIdtPort, LoadKitTagListPort {

    private final KitTagJpaRepository repository;

    @Override
    public List<KitTag> load(long kitId) {
        List<KitTagJpaEntity> entities = repository.findAllByKitId(kitId);
        return entities.stream()
            .map(KitTagMapper::toDomainModel)
            .toList();
    }

    @Override
    public PaginatedResponse<KitTag> load(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KitTagJpaEntity> pageResult = repository.findAll(pageable);
        List<KitTag> items = pageResult.getContent().stream()
            .map(KitTagMapper::toDomainModel)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            KitTagJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }
}
