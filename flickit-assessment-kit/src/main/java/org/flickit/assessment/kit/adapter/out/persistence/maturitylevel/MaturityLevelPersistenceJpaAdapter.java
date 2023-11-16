package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements
    LoadMaturityLevelByKitPort,
    CreateMaturityLevelPort,
    DeleteMaturityLevelPort,
    UpdateMaturityLevelPort {

    private final MaturityLevelJpaRepository repository;
    private final AssessmentKitJpaRepository kitRepository;

    @Override
    public List<MaturityLevel> loadByKitId(Long assessmentKitId) {
        var maturityLevelJpaEntities = repository.findAllByAssessmentKitId(assessmentKitId);
        return maturityLevelJpaEntities.stream()
            .map(MaturityLevelMapper::mapToKitDomainModel)
            .toList();
    }

    @Override
    public void persist(MaturityLevel level, Long kitId) {
        repository.save(MaturityLevelMapper.mapToJpaEntity(level, kitId));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void update(Param param) {
        repository.update(param.title(), param.value());
    }
}
