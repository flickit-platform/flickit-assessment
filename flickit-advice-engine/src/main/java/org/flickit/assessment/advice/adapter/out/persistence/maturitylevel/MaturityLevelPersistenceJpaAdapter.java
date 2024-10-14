package org.flickit.assessment.advice.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("adviceMaturityLevelPersistenceJpaAdapter")
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements LoadMaturityLevelsPort {

    private final MaturityLevelJpaRepository repository;

    @Override
    public List<MaturityLevel> loadAll(long kitVersionId) {
        return repository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(MaturityLevelMapper::mapToDomainModel)
            .toList();
    }
}
