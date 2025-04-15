package org.flickit.assessment.kit.adapter.out.persistence.kitlanguage;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.data.jpa.kit.kitlanguage.KitLanguageJpaEntity;
import org.flickit.assessment.data.jpa.kit.kitlanguage.KitLanguageJpaRepository;
import org.flickit.assessment.kit.application.port.out.kitlanguage.LoadKitLanguagesPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Component
@RequiredArgsConstructor
public class KitLanguagePersistenceJpaAdapter implements
    LoadKitLanguagesPort {

    private final KitLanguageJpaRepository repository;

    @Override
    public Map<Long, List<KitLanguage>> loadByKitIds(List<Long> kitIds) {
        return repository.findAllByKitIdIn(kitIds).stream()
            .collect(groupingBy(KitLanguageJpaEntity::getKitId,
                mapping(e -> KitLanguage.valueOfById(e.getLangId()), toList())));
    }

    @Override
    public List<KitLanguage> loadByKitId(long kitId) {
        return repository.findAllByKitId(kitId).stream()
            .map(e -> KitLanguage.valueOfById(e.getLangId()))
            .toList();
    }
}
