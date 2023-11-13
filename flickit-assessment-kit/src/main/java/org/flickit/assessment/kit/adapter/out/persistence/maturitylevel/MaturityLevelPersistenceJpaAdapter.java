package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.kit.application.domain.Level;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadAssessmentKitMaturityLevelModelsByKitPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements LoadAssessmentKitMaturityLevelModelsByKitPort {

    private final MaturityLevelJpaRepository repository;

    @Override
    public List<Level> load(Long assessmentKitId) {
        var maturityLevelJpaEntities = repository.findByAssessmentKitId(assessmentKitId);
        return maturityLevelJpaEntities.stream()
            .map(MaturityLevelMapper::mapToKitDomainModel)
            .toList();
    }
}
