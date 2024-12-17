package org.flickit.assessment.kit.adapter.out.persistence.maturitylevel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.kit.adapter.out.persistence.levelcompetence.MaturityLevelCompetenceMapper;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static MaturityLevelDslModel mapToDslModel(MaturityLevelJpaEntity entity, Stream<MaturityLevelJpaEntity> maturityLevelStream, Stream<LevelCompetenceJpaEntity> levelCompetenceEntities) {
        Map<Long, Integer> competencesIdToValueMap = levelCompetenceEntities
            .collect(Collectors.toMap(LevelCompetenceJpaEntity::getEffectiveLevelId, LevelCompetenceJpaEntity::getValue));

        Map<Long, String> maturityLevelIdToCodeMap = maturityLevelStream
            .collect(Collectors.toMap(MaturityLevelJpaEntity::getId, MaturityLevelJpaEntity::getCode));

        Map<String, Integer> competencesCodeToValueMap = competencesIdToValueMap.entrySet().stream()
            .filter(entry -> maturityLevelIdToCodeMap.containsKey(entry.getKey()))
            .collect(Collectors.toMap(
                entry -> maturityLevelIdToCodeMap.get(entry.getKey()),
                Map.Entry::getValue
            ));

        return MaturityLevelDslModel.builder()
            .code(entity.getCode())
            .index(entity.getIndex())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .value(entity.getValue())
            .competencesCodeToValueMap(competencesCodeToValueMap)
            .build();

    }
}
