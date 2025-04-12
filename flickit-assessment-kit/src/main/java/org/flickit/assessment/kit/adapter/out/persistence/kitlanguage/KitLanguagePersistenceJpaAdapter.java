package org.flickit.assessment.kit.adapter.out.persistence.kitlanguage;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.data.jpa.kit.kitlanguage.KitLanguageJpaEntity;
import org.flickit.assessment.data.jpa.kit.kitlanguage.KitLanguageJpaRepository;
import org.flickit.assessment.kit.application.port.out.kitlanguage.LoadKitLanguagesPort;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.*;

@Component
@RequiredArgsConstructor
public class KitLanguagePersistenceJpaAdapter implements
    LoadKitLanguagesPort {

    private final KitLanguageJpaRepository repository;

    @Override
    public List<Result> loadByKitIds(List<Long> kitIds) {
        var kitIdToLanguagesMap = repository.findAllByKitIdIn(kitIds).stream()
            .collect(groupingBy(KitLanguageJpaEntity::getKitId,
                mapping(e -> KitLanguage.valueOfById(e.getLangId()), toList())));

        return kitIdToLanguagesMap.entrySet().stream()
            .map(entry -> new Result(entry.getKey(), entry.getValue()))
            .toList();
    }
}
