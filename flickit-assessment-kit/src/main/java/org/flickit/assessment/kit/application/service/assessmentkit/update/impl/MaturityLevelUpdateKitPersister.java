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
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelByCodePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaturityLevelUpdateKitPersister implements UpdateKitPersister {

    private final DeleteMaturityLevelPort deleteMaturityLevelPort;
    private final CreateMaturityLevelPort createMaturityLevelPort;
    private final LoadMaturityLevelByCodePort loadMaturityLevelByCodePort;
    private final UpdateMaturityLevelPort updateMaturityLevelPort;
    private final DeleteLevelCompetencePort deleteLevelCompetencePort;
    private final CreateLevelCompetencePort createLevelCompetencePort;
    private final UpdateLevelCompetencePort updateLevelCompetencePort;

    @Override
    public void persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        List<MaturityLevel> savedLevels = savedKit.getMaturityLevels();
        List<MaturityLevelDslModel> dslLevels = dslKit.getMaturityLevels();

        Map<String, MaturityLevel> savedLevelCodesMap = savedLevels.stream().collect(Collectors.toMap(MaturityLevel::getCode, i -> i));
        Map<String, MaturityLevelDslModel> newDslLevelCodesMap = dslLevels.stream().collect(Collectors.toMap(BaseDslModel::getCode, i -> i));

        List<String> newLevels = newCodesInNewDsl(savedLevelCodesMap.keySet(), newDslLevelCodesMap.keySet());
        List<String> deletedLevels = deletedCodesInNewDsl(savedLevelCodesMap.keySet(), newDslLevelCodesMap.keySet());
        List<String> sameLevels = sameCodesInNewDsl(savedLevelCodesMap.keySet(), newDslLevelCodesMap.keySet());

        newLevels.forEach(i -> createMaturityLevel(newDslLevelCodesMap.get(i), savedKit.getId()));
        deletedLevels.forEach(i -> deleteMaturityLevel(savedLevelCodesMap.get(i), savedKit.getId()));
        sameLevels.forEach(i -> updateMaturityLevel(savedLevelCodesMap.get(i), newDslLevelCodesMap.get(i), savedKit.getId()));
    }

    private List<String> newCodesInNewDsl(Set<String> savedItemCodes, Set<String> newItemCodes) {
        return newItemCodes.stream()
            .filter(i -> savedItemCodes.stream()
                .noneMatch(s -> s.equals(i)))
            .toList();
    }

    private List<String> deletedCodesInNewDsl(Set<String> savedItemCodes, Set<String> newItemCodes) {
        return savedItemCodes.stream()
            .filter(s -> newItemCodes.stream()
                .noneMatch(i -> i.equals(s)))
            .toList();
    }

    private List<String> sameCodesInNewDsl(Set<String> savedItemCodes, Set<String> newItemCodes) {
        return savedItemCodes.stream()
            .filter(s -> newItemCodes.stream()
                .anyMatch(i -> i.equals(s)))
            .toList();
    }

    private void createMaturityLevel(MaturityLevelDslModel newLevel, Long kitId) {
        MaturityLevel newDomainLevel = new MaturityLevel(
            null,
            newLevel.getCode(),
            newLevel.getTitle(),
            newLevel.getIndex(),
            newLevel.getValue(),
            toCompetenceList(newLevel.getCompetencesCodeToValueMap(), kitId)
        );

        Long persistedLevelId = createMaturityLevelPort.persist(newDomainLevel, kitId);
        log.debug("Maturity Level with title [{}] and kit id [{}] is created.", newLevel.getTitle(), kitId);

        List<MaturityLevelCompetence> newCompetences = toCompetenceList(newLevel.getCompetencesCodeToValueMap(), kitId);
        newCompetences.forEach(i -> createLevelCompetence(persistedLevelId, i.getEffectiveLevelId(), i.getValue()));
    }

    private void deleteMaturityLevel(MaturityLevel deletedLevel, Long kitId) {
        deletedLevel.getCompetences()
            .forEach(competence -> deleteLevelCompetence(deletedLevel.getId(), competence.getEffectiveLevelId()));
        deleteMaturityLevelPort.delete(deletedLevel.getId());
        log.debug("Maturity Level with id [{}] and title [{}] in kit with id [{}] is deleted.",
            deletedLevel.getId(), deletedLevel.getTitle(), kitId);
    }

    private void deleteLevelCompetence(Long maturityLevelId, Long effectiveLevelId) {
        deleteLevelCompetencePort.delete(effectiveLevelId, maturityLevelId);
        log.debug("Level Competence with effective level id [{}], maturity level id [{}] is deleted.",
            effectiveLevelId,
            maturityLevelId);
    }

    private List<MaturityLevelCompetence> toCompetenceList(Map<String, Integer> map, Long kitId) {
        return map.keySet().stream()
            .map(key -> new MaturityLevelCompetence(
                loadMaturityLevelByCodePort.loadByCode(key, kitId).getId(),
                key,
                map.get(key)))
            .toList();
    }

    private void updateMaturityLevel(MaturityLevel savedLevel, MaturityLevelDslModel newLevel, Long kitId) {
        if (!newLevel.getTitle().equals(savedLevel.getTitle()) ||
            newLevel.getValue() != savedLevel.getValue() ||
            newLevel.getIndex() != savedLevel.getIndex()) {
            var updateParam = new UpdateMaturityLevelPort.Param(
                kitId,
                newLevel.getCode(),
                newLevel.getTitle(),
                newLevel.getIndex(),
                newLevel.getValue()
            );
            updateMaturityLevelPort.update(updateParam);
            log.debug("A maturity Level with code [{}] is updated.", newLevel.getCode());
        }

        if (newLevel.getCompetencesCodeToValueMap() != null || savedLevel.getCompetences() != null) {
            Map<String, MaturityLevelCompetence> savedCompetenceCodesMap = savedLevel.getCompetences().stream()
                .collect(Collectors.toMap(MaturityLevelCompetence::getEffectiveLevelCode, i -> i));
            Map<String, Integer> competenceCodeToValueMap = newLevel.getCompetencesCodeToValueMap() != null ?
                newLevel.getCompetencesCodeToValueMap() : new HashMap<>();

            List<String> newCompetences = newCodesInNewDsl(savedCompetenceCodesMap.keySet(), competenceCodeToValueMap.keySet());
            List<String> deletedCompetences = deletedCodesInNewDsl(savedCompetenceCodesMap.keySet(), competenceCodeToValueMap.keySet());
            List<String> sameCompetences = sameCodesInNewDsl(savedCompetenceCodesMap.keySet(), competenceCodeToValueMap.keySet());

            newCompetences.forEach(i -> createLevelCompetence(
                savedLevel.getId(),
                loadMaturityLevelByCodePort.loadByCode(i, kitId).getId(),
                competenceCodeToValueMap.get(i)));

            deletedCompetences.forEach(i -> deleteLevelCompetence(
                savedLevel.getId(),
                loadMaturityLevelByCodePort.loadByCode(i, kitId).getId()));

            sameCompetences.forEach(i -> {
                if (savedCompetenceCodesMap.get(i).getValue() != competenceCodeToValueMap.get(i)) {
                    updateLevelCompetence(
                        savedLevel.getId(),
                        loadMaturityLevelByCodePort.loadByCode(i, kitId).getId(),
                        competenceCodeToValueMap.get(i));
                }
            });
        }
    }

    private void createLevelCompetence(long affectedLevelId, long effectiveLevelId, int newCompetenceValue) {
        createLevelCompetencePort.persist(
            affectedLevelId,
            effectiveLevelId,
            newCompetenceValue);
        log.debug("Level Competence with effective level id [{}] is created.", effectiveLevelId);
    }

    private void updateLevelCompetence(long affectedLevelId, long effectiveLevelId, int newValue) {
        updateLevelCompetencePort.update(
            affectedLevelId,
            effectiveLevelId,
            newValue);
        log.debug("Level Competence with effective level id [{}], maturity level id [{}] is updated.",
            effectiveLevelId,
            affectedLevelId);
    }
}
