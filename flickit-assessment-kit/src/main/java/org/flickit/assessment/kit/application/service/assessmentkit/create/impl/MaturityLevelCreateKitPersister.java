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
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitId) {
        List<MaturityLevelDslModel> dslLevels = dslKit.getMaturityLevels();
        Map<String, MaturityLevelDslModel> dslLevelCodesMap = dslLevels.stream().collect(toMap(BaseDslModel::getCode, i -> i));
        Map<String, MaturityLevel> codeToPersistedLevels = new HashMap<>();
        dslLevels.forEach(ml -> {
            MaturityLevel createdLevel = createMaturityLevel(dslLevelCodesMap.get(ml.getCode()), kitId);
            codeToPersistedLevels.put(createdLevel.getCode(), createdLevel);
        });

        // create competences of new levels
        dslLevels.forEach(ml -> {
            MaturityLevel affectedLevel = codeToPersistedLevels.get(ml.getCode());
            MaturityLevelDslModel dslLevel = dslLevelCodesMap.get(ml.getCode());

            Map<String, Integer> dslLevelCompetenceCodes = dslLevel.getCompetencesCodeToValueMap() != null ?
                dslLevel.getCompetencesCodeToValueMap() : Map.of();

            dslLevelCompetenceCodes.forEach((key, value) -> {
                Long effectiveLevelId = codeToPersistedLevels.get(key).getId();
                createLevelCompetence(affectedLevel.getId(), effectiveLevelId, value);
            });
        });

        Map<String, MaturityLevel> levelCodeToIdMap = codeToPersistedLevels.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        ctx.put(KEY_MATURITY_LEVELS, levelCodeToIdMap);
        log.debug("Final Levels: {}", levelCodeToIdMap);
    }

    private MaturityLevel createMaturityLevel(MaturityLevelDslModel newLevel, Long kitId) {
        MaturityLevel newDomainLevel = new MaturityLevel(
            null,
            newLevel.getCode(),
            newLevel.getTitle(),
            newLevel.getIndex(),
            newLevel.getValue(),
            null
        );

        Long persistedLevelId = createMaturityLevelPort.persist(newDomainLevel, kitId);
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

    private void createLevelCompetence(long affectedLevelId, long effectiveLevelId, int value) {
        createLevelCompetencePort.persist(affectedLevelId, effectiveLevelId, value);
        log.debug("LevelCompetence[affectedId={}, effectiveId={}, value={}] created.", affectedLevelId, effectiveLevelId, value);
    }

}
