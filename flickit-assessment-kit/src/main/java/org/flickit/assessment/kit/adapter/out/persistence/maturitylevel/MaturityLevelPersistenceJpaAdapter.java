package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements
    CreateMaturityLevelPort,
    DeleteMaturityLevelPort,
    UpdateMaturityLevelPort,
    LoadMaturityLevelPort {

    private final MaturityLevelJpaRepository repository;

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
        repository.update(param.id(), param.title(), param.index(), param.value());
    }

    @Override
    public Optional<MaturityLevel> load(Long id) {
        Optional<MaturityLevelJpaEntity> entity = repository.findById(id);
        return entity.map(MaturityLevelMapper::mapToDomainModel);
    }
}
