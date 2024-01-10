package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements
    CreateMaturityLevelPort,
    DeleteMaturityLevelPort,
    UpdateMaturityLevelPort {

    private final MaturityLevelJpaRepository repository;

    @Override
    public Long persist(MaturityLevel level, Long kitId, UUID currentUserId) {
        return repository.save(MaturityLevelMapper.mapToJpaEntity(level, kitId, currentUserId)).getId();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void update(List<MaturityLevel> maturityLevels, UUID currentUserId) {
        Map<Long, MaturityLevel> idToModel = maturityLevels.stream().collect(toMap(MaturityLevel::getId, x -> x));
        List<MaturityLevelJpaEntity> entities = repository.findAllById(idToModel.keySet());
        entities.forEach(x -> {
            MaturityLevel newLevel = idToModel.get(x.getId());
            x.setIndex(newLevel.getIndex());
            x.setTitle(newLevel.getTitle());
            x.setValue(newLevel.getValue());
            x.setLastModificationTime(LocalDateTime.now());
            x.setLastModifiedBy(currentUserId);
        });
        repository.saveAll(entities);
        repository.flush();
    }
}
