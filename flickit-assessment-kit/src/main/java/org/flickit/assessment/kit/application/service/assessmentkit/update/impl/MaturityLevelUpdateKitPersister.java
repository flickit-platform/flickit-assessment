package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_MATURITY_LEVELS;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaturityLevelUpdateKitPersister implements UpdateKitPersister {

    private final DeleteMaturityLevelPort deleteMaturityLevelPort;
    private final CreateMaturityLevelPort createMaturityLevelPort;
    private final UpdateMaturityLevelPort updateMaturityLevelPort;
    private final DeleteLevelCompetencePort deleteLevelCompetencePort;
    private final CreateLevelCompetencePort createLevelCompetencePort;
    private final UpdateLevelCompetencePort updateLevelCompetencePort;

    @Override
    public int order() {
        return 1;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx,
                                            AssessmentKit savedKit,
                                            AssessmentKitDslModel dslKit,
                                            UUID currentUserId) {
        List<MaturityLevel> savedLevels = savedKit.getMaturityLevels();
        List<MaturityLevelDslModel> dslLevels = dslKit.getMaturityLevels();

        Map<String, MaturityLevel> savedLevelCodesMap = savedLevels.stream().collect(Collectors.toMap(MaturityLevel::getCode, i -> i));
        Map<String, MaturityLevelDslModel> dslLevelCodesMap = dslLevels.stream().collect(Collectors.toMap(BaseDslModel::getCode, i -> i));

        Set<String> deletedLevels = deletedCodesInNewDsl(savedLevelCodesMap.keySet(), dslLevelCodesMap.keySet());
        Set<String> existingLevels = sameCodesInNewDsl(savedLevelCodesMap.keySet(), dslLevelCodesMap.keySet());
        Set<String> newLevels = newCodesInNewDsl(savedLevelCodesMap.keySet(), dslLevelCodesMap.keySet());

        Map<String, MaturityLevel> codeToPersistedLevels = new HashMap<>();

        deletedLevels.forEach(i -> deleteMaturityLevel(savedLevelCodesMap.get(i), savedKit.getKitVersionId()));

        boolean existingLevelValueUpdated = false;
        List<MaturityLevel> updatedLevels = new ArrayList<>();
        for (String code : existingLevels) {
            MaturityLevel existingLevel = savedLevelCodesMap.get(code);
            MaturityLevelDslModel updatingLevel = dslLevelCodesMap.get(code);
            if (existingLevel.getValue() != updatingLevel.getValue())
                existingLevelValueUpdated = true;

            if (isMaturityLevelUpdated(existingLevel, updatingLevel)) {
                MaturityLevel updatedLevel = createUpdatedLevel(existingLevel, updatingLevel);
                updatedLevels.add(updatedLevel);
                log.debug("MaturityLevel[id={}, code={}] updated.", existingLevel.getId(), existingLevel.getCode());
                codeToPersistedLevels.put(existingLevel.getCode(), updatedLevel);
            } else {
                codeToPersistedLevels.put(existingLevel.getCode(), existingLevel);
            }
        }
        updateMaturityLevelPort.update(updatedLevels, savedKit.getKitVersionId(), currentUserId);

        newLevels.forEach(code -> {
            MaturityLevel createdLevel = createMaturityLevel(dslLevelCodesMap.get(code), savedKit.getKitVersionId(), currentUserId);
            codeToPersistedLevels.put(createdLevel.getCode(), createdLevel);
        });

        // update competences of existing levels
        boolean isCompetencesChanged = updateCompetencesToExistingLevels(savedKit.getKitVersionId(),
            savedLevelCodesMap,
            dslLevelCodesMap,
            existingLevels,
            codeToPersistedLevels,
            currentUserId);

        // create competences of new levels
        newLevels.forEach(code -> {
            MaturityLevel affectedLevel = codeToPersistedLevels.get(code);
            MaturityLevelDslModel dslLevel = dslLevelCodesMap.get(code);

            Map<String, Integer> dslLevelCompetenceCodes = dslLevel.getCompetencesCodeToValueMap() != null ?
                dslLevel.getCompetencesCodeToValueMap() : Map.of();

            dslLevelCompetenceCodes.forEach((key, value) -> {
                Long effectiveLevelId = codeToPersistedLevels.get(key).getId();
                createLevelCompetence(affectedLevel.getId(), effectiveLevelId, value, savedKit.getKitVersionId(), currentUserId);
            });
        });

        Map<String, Long> levelCodeToIdMap = codeToPersistedLevels.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getId()));
        ctx.put(KEY_MATURITY_LEVELS, levelCodeToIdMap);
        log.debug("Final Levels: {}", levelCodeToIdMap);

        boolean isMajorUpdate = !newLevels.isEmpty() || !deletedLevels.isEmpty() || existingLevelValueUpdated || isCompetencesChanged;
        log.debug("InvalidateResult is [{}]: newLevels[{}], deletedLevels[{}], updatedLevels[{}], changedCompetences[{}]",
            isMajorUpdate, !newLevels.isEmpty(), !deletedLevels.isEmpty(), existingLevelValueUpdated, isCompetencesChanged);
        return new UpdateKitPersisterResult(isMajorUpdate);
    }

    private MaturityLevel createMaturityLevel(MaturityLevelDslModel newLevel, Long kitVersionId, UUID currentUserId) {
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
        log.debug("MaturityLevel[id={}, code={}] created.", persistedLevelId, newLevel.getTitle());

        return new MaturityLevel(
            persistedLevelId,
            newLevel.getCode(),
            newLevel.getTitle(),
            newLevel.getIndex(),
            newLevel.getDescription(),
            newLevel.getValue(),
            null
        );
    }

    private static boolean isMaturityLevelUpdated(MaturityLevel savedLevel, MaturityLevelDslModel newLevel) {
        return !Objects.equals(newLevel.getTitle(), savedLevel.getTitle()) ||
            !Objects.equals(newLevel.getDescription(), savedLevel.getDescription()) ||
            newLevel.getValue() != savedLevel.getValue() ||
            newLevel.getIndex() != savedLevel.getIndex();
    }

    private static MaturityLevel createUpdatedLevel(MaturityLevel savedLevel, MaturityLevelDslModel newLevel) {
        return new MaturityLevel(
            savedLevel.getId(),
            savedLevel.getCode(),
            newLevel.getTitle(),
            newLevel.getIndex(),
            newLevel.getDescription(),
            newLevel.getValue(),
            null
        );
    }

    private boolean updateCompetencesToExistingLevels(Long kitVersionId,
                                                      Map<String, MaturityLevel> savedLevelCodesMap,
                                                      Map<String, MaturityLevelDslModel> dslLevelCodesMap,
                                                      Set<String> existingLevels,
                                                      Map<String, MaturityLevel> codeToPersistedLevels,
                                                      UUID currentUserId) {
        boolean isCompetencesChanged = false;
        Map<Long, String> idToCodeMap = codeToPersistedLevels.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getValue().getId(),
                Map.Entry::getKey
            ));
        for (String code : existingLevels) {
            MaturityLevel affectedLevel = codeToPersistedLevels.get(code);
            MaturityLevelDslModel dslLevel = dslLevelCodesMap.get(code);

            Set<String> dslLevelCompetenceCodes = dslLevel.getCompetencesCodeToValueMap() != null ?
                dslLevel.getCompetencesCodeToValueMap().keySet() : Set.of();

            Map<String, Integer> existingLevelCompetenceCodeToValue = savedLevelCodesMap.get(code).getCompetences().stream()
                .filter(i -> idToCodeMap.get(i.getEffectiveLevelId()) != null)
                .collect(
                    toMap(i -> idToCodeMap.get(i.getEffectiveLevelId()), MaturityLevelCompetence::getValue));

            Set<String> newCompetences = newCodesInNewDsl(existingLevelCompetenceCodeToValue.keySet(), dslLevelCompetenceCodes);
            Set<String> deletedCompetences = deletedCodesInNewDsl(existingLevelCompetenceCodeToValue.keySet(), dslLevelCompetenceCodes);
            Set<String> sameCompetences = sameCodesInNewDsl(existingLevelCompetenceCodeToValue.keySet(), dslLevelCompetenceCodes);

            // create new competences
            newCompetences.forEach(cmpCode -> {
                Long effectiveLevelId = codeToPersistedLevels.get(cmpCode).getId();
                Integer value = dslLevel.getCompetencesCodeToValueMap().get(cmpCode);
                createLevelCompetence(affectedLevel.getId(), effectiveLevelId, value, kitVersionId, currentUserId);
            });

            // delete removed competences
            deletedCompetences.forEach(cmpCode -> {
                    Long effectiveLevelId = savedLevelCodesMap.get(cmpCode).getId();
                    deleteLevelCompetence(affectedLevel.getId(), effectiveLevelId, kitVersionId);
                }
            );

            // update existing competences
            for (String cmpCode : sameCompetences) {
                int oldValue = existingLevelCompetenceCodeToValue.get(cmpCode);
                int newValue = dslLevel.getCompetencesCodeToValueMap().get(cmpCode);
                Long effectiveLevelId = codeToPersistedLevels.get(cmpCode).getId();
                if (oldValue != newValue) {
                    isCompetencesChanged = true;
                    updateLevelCompetence(affectedLevel.getId(), effectiveLevelId, kitVersionId, newValue, currentUserId);
                }
            }

            isCompetencesChanged = !newCompetences.isEmpty() || !deletedCompetences.isEmpty() || isCompetencesChanged;
        }
        return isCompetencesChanged;
    }

    private void createLevelCompetence(long affectedLevelId, long effectiveLevelId, int value, Long kitVersionId, UUID currentUserId) {
        createLevelCompetencePort.persist(affectedLevelId, effectiveLevelId, value, kitVersionId, currentUserId);
        log.debug("LevelCompetence[affectedId={}, effectiveId={}, value={}] created.", affectedLevelId, effectiveLevelId, value);
    }

    private void deleteMaturityLevel(MaturityLevel deletedLevel, Long kitVersionId) {
        deleteMaturityLevelPort.delete(deletedLevel.getId(), kitVersionId);
        log.debug("MaturityLevel[id={}, code={}] deleted from kitVersionId[{}].", deletedLevel.getId(), deletedLevel.getCode(), kitVersionId);
    }

    private void deleteLevelCompetence(Long affectedLevelId, Long effectiveLevelId, Long kitVersionId) {
        deleteLevelCompetencePort.delete(affectedLevelId, effectiveLevelId, kitVersionId);
        log.debug("LevelCompetence[affectedId={}, effectiveId={}] deleted.", affectedLevelId, effectiveLevelId);
    }

    private void updateLevelCompetence(long affectedLevelId, long effectiveLevelId, Long kitVersionId, int value, UUID currentUserId) {
        updateLevelCompetencePort.update(affectedLevelId, effectiveLevelId, kitVersionId, value, currentUserId);
        log.debug("LevelCompetence[affectedId={}, effectiveId={}, value={}] updated.", affectedLevelId, effectiveLevelId, value);
    }

    private Set<String> newCodesInNewDsl(Set<String> savedItemCodes, Set<String> newItemCodes) {
        return newItemCodes.stream()
            .filter(i -> !savedItemCodes.contains(i))
            .collect(toSet());
    }

    private Set<String> deletedCodesInNewDsl(Set<String> savedItemCodes, Set<String> newItemCodes) {
        return savedItemCodes.stream()
            .filter(s -> !newItemCodes.contains(s))
            .collect(toSet());
    }

    private Set<String> sameCodesInNewDsl(Set<String> savedItemCodes, Set<String> newItemCodes) {
        return savedItemCodes.stream()
            .filter(newItemCodes::contains)
            .collect(toSet());
    }
}
