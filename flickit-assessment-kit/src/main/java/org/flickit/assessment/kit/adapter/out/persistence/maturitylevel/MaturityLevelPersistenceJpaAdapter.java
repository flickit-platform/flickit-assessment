package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity.EntityId;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.levelcompetence.MaturityLevelCompetenceMapper;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;
import org.flickit.assessment.kit.application.port.out.maturitylevel.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.adapter.out.persistence.maturitylevel.MaturityLevelMapper.mapToJpaEntityToPersist;
import static org.flickit.assessment.kit.common.ErrorMessageKey.MATURITY_LEVEL_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class MaturityLevelPersistenceJpaAdapter implements
    CreateMaturityLevelPort,
    DeleteMaturityLevelPort,
    UpdateMaturityLevelPort,
    LoadAllMaturityLevelsPort,
    LoadAttributeMaturityLevelsPort,
    LoadMaturityLevelsPort {

    private final MaturityLevelJpaRepository repository;
    private final LevelCompetenceJpaRepository levelCompetenceRepository;

    @Override
    public Long persist(MaturityLevel level, Long kitVersionId, UUID createdBy) {
        return repository.save(mapToJpaEntityToPersist(level, kitVersionId, createdBy)).getId();
    }

    @Override
    public void delete(Long id, Long kitVersionId) {
        if(!repository.existsByIdAndKitVersionId(id, kitVersionId))
            throw new ResourceNotFoundException(MATURITY_LEVEL_ID_NOT_FOUND);
        repository.deleteByIdAndKitVersionId(id, kitVersionId);
    }

    @Override
    public void updateAll(List<MaturityLevel> maturityLevels, Long kitVersionId, UUID lastModifiedBy) {
        Map<EntityId, MaturityLevel> idToModel = maturityLevels.stream()
            .collect(Collectors.toMap(
                ml -> new EntityId(ml.getId(), kitVersionId),
                ml -> ml
            ));
        List<MaturityLevelJpaEntity> entities = repository.findAllById(idToModel.keySet());
        entities.forEach(x -> {
            MaturityLevel newLevel = idToModel.get(new EntityId(x.getId(), kitVersionId));
            x.setIndex(newLevel.getIndex());
            x.setTitle(newLevel.getTitle());
            x.setValue(newLevel.getValue());
            x.setDescription(newLevel.getDescription());
            x.setLastModificationTime(LocalDateTime.now());
            x.setLastModifiedBy(lastModifiedBy);
        });
        repository.saveAll(entities);
        repository.flush();
    }

    @Override
    public void update(MaturityLevel maturityLevel, Long kitVersionId, LocalDateTime lastModificationTime, UUID lastModifiedBy ) {
        if (!repository.existsByIdAndKitVersionId(maturityLevel.getId(), kitVersionId))
            throw new ResourceNotFoundException(MATURITY_LEVEL_ID_NOT_FOUND);

        repository.update(maturityLevel.getId(), kitVersionId, maturityLevel.getTitle(), maturityLevel.getIndex(), maturityLevel.getCode(),
            maturityLevel.getDescription(), maturityLevel.getValue(), lastModificationTime, lastModifiedBy);
    }

    @Override
    public List<MaturityLevel> loadByKitVersionId(Long kitVersionId) {
        List<MaturityLevelJpaEntity> maturityLevelEntities = repository.findAllByKitVersionIdOrderByIndex(kitVersionId, null).getContent();

        List<Long> levelIds = maturityLevelEntities.stream()
            .map(MaturityLevelJpaEntity::getId)
            .toList();

        List<LevelCompetenceJpaEntity> levelCompetenceEntities = levelCompetenceRepository.findAllByAffectedLevelIdInAndKitVersionId(levelIds, kitVersionId);
        Map<Long, List<LevelCompetenceJpaEntity>> levelIdToLevelCompetences = levelCompetenceEntities.stream()
            .collect(Collectors.groupingBy(LevelCompetenceJpaEntity::getAffectedLevelId));

        return maturityLevelEntities.stream()
            .map(e -> {
                MaturityLevel maturityLevel = MaturityLevelMapper.mapToDomainModel(e);
                if (levelIdToLevelCompetences.containsKey(e.getId())) {
                    List<LevelCompetenceJpaEntity> levelCompetenceJpaEntities = levelIdToLevelCompetences.get(maturityLevel.getId());
                    List<MaturityLevelCompetence> maturityLevelCompetences = levelCompetenceJpaEntities.stream()
                        .map(MaturityLevelCompetenceMapper::mapToDomainModel)
                        .toList();
                    maturityLevel.setCompetences(maturityLevelCompetences);
                } else
                    maturityLevel.setCompetences(new ArrayList<>());
                return maturityLevel;
            }).toList();
    }

    @Override
    public List<LoadAttributeMaturityLevelsPort.Result> loadAttributeLevels(long attributeId, long kitVersionId) {
        return repository.loadAttributeLevels(attributeId, kitVersionId).stream()
            .map(e -> new LoadAttributeMaturityLevelsPort.Result(e.getId(), e.getTitle(), e.getIndex(), e.getQuestionCount()))
            .toList();
    }

    @Override
    public PaginatedResponse<MaturityLevel> loadByKitVersionId(long kitVersionId, Integer size, Integer page) {
        var pageResult = repository.findAllByKitVersionIdOrderByIndex(kitVersionId, PageRequest.of(page, size));

        List<Long> levelIds = pageResult.stream()
            .map(MaturityLevelJpaEntity::getId)
            .toList();

        List<LevelCompetenceJpaEntity> levelCompetenceEntities = levelCompetenceRepository.findAllByAffectedLevelIdInAndKitVersionId(levelIds, kitVersionId);
        Map<Long, List<LevelCompetenceJpaEntity>> levelIdToLevelCompetences = levelCompetenceEntities.stream()
            .collect(Collectors.groupingBy(LevelCompetenceJpaEntity::getAffectedLevelId));

        var maturityLevels = pageResult.stream()
            .map(e -> {
                MaturityLevel maturityLevel = MaturityLevelMapper.mapToDomainModel(e);
                if (levelIdToLevelCompetences.containsKey(e.getId())) {
                    List<LevelCompetenceJpaEntity> levelCompetenceJpaEntities = levelIdToLevelCompetences.get(maturityLevel.getId());
                    List<MaturityLevelCompetence> maturityLevelCompetences = levelCompetenceJpaEntities.stream()
                        .map(MaturityLevelCompetenceMapper::mapToDomainModel)
                        .toList();
                    maturityLevel.setCompetences(maturityLevelCompetences);
                } else
                    maturityLevel.setCompetences(new ArrayList<>());
                return maturityLevel;
            }).toList();

        return new PaginatedResponse<>(maturityLevels,
            pageResult.getNumber(),
            pageResult.getSize(),
            MaturityLevelJpaEntity.Fields.index,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements());
    }
}
