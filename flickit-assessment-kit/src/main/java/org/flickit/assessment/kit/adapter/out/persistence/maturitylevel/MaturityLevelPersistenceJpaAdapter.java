package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.out.maturitylevel.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements
    LoadMaturityLevelByKitPort,
    CreateMaturityLevelPort,
    DeleteMaturityLevelPort,
    UpdateMaturityLevelPort,
    LoadMaturityLevelByCodePort {

    private final MaturityLevelJpaRepository repository;

    @Override
    public List<MaturityLevel> loadByKitId(Long assessmentKitId) {
        var maturityLevelJpaEntities = repository.findAllByAssessmentKitId(assessmentKitId);
        return maturityLevelJpaEntities.stream()
            .map(MaturityLevelMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public Long persist(MaturityLevel level, Long kitId) {
        return repository.save(MaturityLevelMapper.mapToJpaEntity(level, kitId)).getId();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void update(Param param) {
        repository.update(param.kitId(), param.code(), param.title(), param.index(), param.value());
    }

    @Override
    public MaturityLevel loadByCode(String code, Long kitId) {
        return MaturityLevelMapper.mapToDomainModel(repository.findByCodeAndAssessmentKitId(code, kitId));
    }
}
