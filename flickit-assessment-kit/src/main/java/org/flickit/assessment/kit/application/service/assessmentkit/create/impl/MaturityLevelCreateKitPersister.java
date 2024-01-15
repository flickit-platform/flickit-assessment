package org.flickit.assessment.kit.application.service.assessmentkit.create.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_MATURITY_LEVELS;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaturityLevelCreateKitPersister implements CreateKitPersister {

    private final CreateMaturityLevelPort createMaturityLevelPort;
    private final CreateLevelCompetencePort createLevelCompetencePort;

    @Override
    public int order() {
        return 1;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitId, UUID currentUserId) {
        List<MaturityLevelDslModel> dslLevels = dslKit.getMaturityLevels();

        Map<String, Map<String, Integer>> dslLevelCodeToCompetencesMap = dslLevels.stream().collect(toMap(
            BaseDslModel::getCode,
            l -> l.getCompetencesCodeToValueMap() != null ? l.getCompetencesCodeToValueMap() : Map.of()));

        Map<String, MaturityLevel> levelCodeToPersistedLevels = new HashMap<>();
        dslLevels.forEach(ml -> {
            MaturityLevel createdLevel = createMaturityLevel(ml, kitId, currentUserId);
            levelCodeToPersistedLevels.put(createdLevel.getCode(), createdLevel);
        });

        // create competences of new levels
        dslLevelCodeToCompetencesMap.keySet().forEach(levelCode -> {
            MaturityLevel affectedLevel = levelCodeToPersistedLevels.get(levelCode);
            Map<String, Integer> dslCompetenceCodes = dslLevelCodeToCompetencesMap.get(levelCode);

            dslCompetenceCodes.forEach((key, value) -> {
                Long effectiveLevelId = levelCodeToPersistedLevels.get(key).getId();
                createLevelCompetence(affectedLevel.getId(), effectiveLevelId, value, currentUserId);
            });
        });

        Map<String, Long> levelCodeToIdMap = levelCodeToPersistedLevels.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getId()));
        ctx.put(KEY_MATURITY_LEVELS, levelCodeToIdMap);
        log.debug("Final Levels: {}", levelCodeToIdMap);
    }

    private MaturityLevel createMaturityLevel(MaturityLevelDslModel newLevel, Long kitId, UUID currentUserId) {
        MaturityLevel newDomainLevel = new MaturityLevel(
            null,
            newLevel.getCode(),
            newLevel.getTitle(),
            newLevel.getIndex(),
            newLevel.getValue(),
            null
        );

        Long persistedLevelId = createMaturityLevelPort.persist(newDomainLevel, kitId, currentUserId);
        log.debug("MaturityLevel[id={}, code={}] created.", persistedLevelId, newLevel.getCode());

        return new MaturityLevel(
            persistedLevelId,
            newLevel.getCode(),
            newLevel.getTitle(),
            newLevel.getIndex(),
            newLevel.getValue(),
            null
        );
    }

    private void createLevelCompetence(long affectedLevelId, long effectiveLevelId, int value, UUID currentUserId) {
        createLevelCompetencePort.persist(affectedLevelId, effectiveLevelId, value, currentUserId);
        log.debug("LevelCompetence[affectedId={}, effectiveId={}, value={}] created.", affectedLevelId, effectiveLevelId, value);
    }

}
