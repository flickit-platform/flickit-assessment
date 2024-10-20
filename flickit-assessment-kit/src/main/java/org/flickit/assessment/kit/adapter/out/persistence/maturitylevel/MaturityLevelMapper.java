package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.kit.adapter.out.persistence.levelcompetence.MaturityLevelCompetenceMapper;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaturityLevelMapper {

    public static MaturityLevel mapToDomainModel(MaturityLevelJpaEntity entity) {
        return new MaturityLevel(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getDescription(),
            entity.getValue()
        );
    }

    public static List<MaturityLevel> mapToDomainModel(List<MaturityLevelJpaEntity> maturityLevelEntities, List<LevelCompetenceJpaEntity> levelCompetenceEntities) {
        var levelIdToLevelCompetences = levelCompetenceEntities.stream()
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

    public static MaturityLevelJpaEntity mapToJpaEntityToPersist(MaturityLevel level, Long kitVersionId, UUID createdBy) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new MaturityLevelJpaEntity(
            null,
            kitVersionId,
            level.getCode(),
            level.getIndex(),
            level.getTitle(),
            level.getDescription(),
            level.getValue(),
            creationTime,
            creationTime,
            createdBy,
            createdBy
        );
    }
}
