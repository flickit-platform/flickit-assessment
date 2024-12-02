package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_MATURITY_LEVELS;

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
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitVersionId, UUID currentUserId) {
        List<MaturityLevelDslModel> dslLevels = dslKit.getMaturityLevels();

        Map<String, Map<String, Integer>> dslLevelCodeToCompetencesMap = dslLevels.stream().collect(toMap(
            BaseDslModel::getCode,
            l -> l.getCompetencesCodeToValueMap() != null ? l.getCompetencesCodeToValueMap() : Map.of()));

        Map<String, Long> levelCodeToPersistedLevelId = new HashMap<>();
        dslLevels.forEach(ml -> {
            Long persistedLevelId = createMaturityLevel(ml, kitVersionId, currentUserId);
            levelCodeToPersistedLevelId.put(ml.getCode(), persistedLevelId);
        });

        // create competences of new levels
        dslLevelCodeToCompetencesMap.keySet().forEach(levelCode -> {
            Long affectedLevelId = levelCodeToPersistedLevelId.get(levelCode);
            Map<String, Integer> dslCompetenceCodes = dslLevelCodeToCompetencesMap.get(levelCode);

            dslCompetenceCodes.forEach((key, value) -> {
                Long effectiveLevelId = levelCodeToPersistedLevelId.get(key);
                createLevelCompetence(affectedLevelId, effectiveLevelId, value, kitVersionId, currentUserId);
            });
        });

        Map<String, Long> levelCodeToIdMap = levelCodeToPersistedLevelId.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        ctx.put(KEY_MATURITY_LEVELS, levelCodeToIdMap);
        log.debug("Final Levels: {}", levelCodeToIdMap);
    }

    private Long createMaturityLevel(MaturityLevelDslModel newLevel, Long kitVersionId, UUID currentUserId) {
        MaturityLevel newDomainLevel = new MaturityLevel(
            null,
            newLevel.getCode(),
            newLevel.getTitle(),
            newLevel.getIndex(),
            newLevel.getDescription(),
            newLevel.getValue(),
            null
        );

        Long persistedLevelId = createMaturityLevelPort.persist(newDomainLevel, kitVersionId, currentUserId);
        log.debug("MaturityLevel[id={}, code={}] created.", persistedLevelId, newLevel.getCode());

        return persistedLevelId;
    }

    private void createLevelCompetence(long affectedLevelId, long effectiveLevelId, int value, Long kitVersionId, UUID currentUserId) {
        createLevelCompetencePort.persist(affectedLevelId, effectiveLevelId, value, kitVersionId, currentUserId);
        log.debug("LevelCompetence[affectedId={}, effectiveId={}, value={}] created.", affectedLevelId, effectiveLevelId, value);
    }

}
