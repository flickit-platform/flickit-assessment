package org.flickit.assessment.core.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadAssessmentKitMaturityLevelModelsByKitPort;
import org.flickit.assessment.data.jpa.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.kit.domain.Level;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements LoadAssessmentKitMaturityLevelModelsByKitPort {

    private final MaturityLevelJpaRepository repository;

    @Override
    public List<Level> load(Long assessmentKitId) {
        var maturityLevelJpaEntities = repository.loadByAssessmentKitId(assessmentKitId);
        return maturityLevelJpaEntities.stream()
            .map(MaturityLevelMapper::mapToKitDomainModel)
            .toList();
    }
}
