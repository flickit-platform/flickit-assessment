package org.flickit.assessment.kit.application.service.assessmentkit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.flickit.assessment.kit.application.exception.NotValidKitChangesException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelByTitlePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.flickit.assessment.kit.application.service.DslTranslator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_KIT_CHANGE_NOT_VALID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateKitByDslService implements UpdateKitByDslUseCase {

    private final DslTranslator dslTranslator;

    private final LoadAssessmentKitInfoPort loadAssessmentKitInfoPort;
    private final DeleteMaturityLevelPort deleteMaturityLevelPort;
    private final CreateMaturityLevelPort createMaturityLevelPort;
    private final LoadMaturityLevelByTitlePort loadMaturityLevelByTitlePort;
    private final UpdateMaturityLevelPort updateMaturityLevelPort;
    private final DeleteLevelCompetencePort deleteLevelCompetencePort;
    private final CreateLevelCompetencePort createLevelCompetencePort;
    private final UpdateLevelCompetencePort updateLevelCompetencePort;

    @Override
    public void update(Param param) {
        AssessmentKit loadedKit = loadAssessmentKitInfoPort.load(param.getKitId());
        AssessmentKitDslModel kitDslModel = dslTranslator.parseJson(param.getDslContent());

        validateChanges(kitDslModel, loadedKit);

        checkMaturityLevel(param.getKitId(), loadedKit.getMaturityLevels(), kitDslModel.getMaturityLevels());
    }

    /**
     * validateSubjectChanges();
     * validateAttributeChanges();
     * validateQuestionnaireChanges();
     * validateQuestionChanges();
     * @param kitModel
     * @param loadedKit
     * @Throws NotValidKitChangesException
     */
    private void validateChanges(AssessmentKitDslModel kitModel, AssessmentKit loadedKit) {
    }

    private void checkMaturityLevel(Long kitId, List<MaturityLevel> loadedLevels, List<MaturityLevelDslModel> levelModels) {
        deleteMaturityLevel(loadedLevels, levelModels, kitId);
        createMaturityLevel(loadedLevels, levelModels, kitId);
        updateMaturityLevel(loadedLevels, levelModels, kitId);

        for (MaturityLevelDslModel newLevel : levelModels) {
            for (MaturityLevel loadedLevel : loadedLevels) {
                if (newLevel.getCode().equals(loadedLevel.getCode())) {
                    if (newLevel.getCompetencesCodeToValueMap() != null && loadedLevel.getCompetences() != null) {
                        var newCompetences = toCompetenceList(newLevel.getCompetencesCodeToValueMap(), kitId);
                        var loadedCompetences = loadedLevel.getCompetences();
                        deleteLevelCompetence(loadedLevel.getId(), loadedCompetences, newCompetences, kitId);
                        createLevelCompetence(newLevel.getCode(), newCompetences, loadedCompetences, kitId);
                        updateLevelCompetence(loadedLevel.getId(), newCompetences, loadedCompetences, kitId);
                    }
                }
            }
        }

    }

    private void deleteMaturityLevel(List<MaturityLevel> loadedLevels, List<MaturityLevelDslModel> levelModels, Long kitId) {
        List<MaturityLevel> mustBeDeletedLevels = loadedLevels.stream()
            .filter(level -> levelModels.stream()
                .noneMatch(levelModel -> levelModel.getCode().equals(level.getCode())))
            .toList();
        mustBeDeletedLevels.forEach(level -> {
            level.getCompetences()
                .forEach(competence -> deleteLevelCompetence(level.getId(), kitId, competence.getEffectiveLevelId()));
            deleteMaturityLevelPort.delete(level.getId());
            log.warn("Maturity Level with id [{}] and title [{}] in kit with id [{}] is deleted.",
                level.getId(), level.getTitle(), kitId);
        });
    }

    private void deleteLevelCompetence(Long maturityLevelId, Long kitId, Long effectiveLevelId) {
        deleteLevelCompetencePort.delete(effectiveLevelId, maturityLevelId, kitId);
        log.warn("Level Competence with effective level id [{}], maturity level id [{}] and kit id [{}] is deleted.",
            effectiveLevelId,
            maturityLevelId,
            kitId);
    }

    private void createMaturityLevel(List<MaturityLevel> loadedLevels, List<MaturityLevelDslModel> levelModels, Long kitId) {
        List<MaturityLevelDslModel> newLevels = levelModels.stream()
            .filter(level -> loadedLevels.stream()
                .noneMatch(loadedLevel -> loadedLevel.getCode().equals(level.getCode())))
            .toList();
        newLevels.forEach(level -> {
            createMaturityLevelPort.persist(toMaturityLevel(level, kitId), kitId);
            log.warn("Maturity Level with title [{}] and kit id [{}] is created.", level.getTitle(), kitId);
        });
    }

    private MaturityLevel toMaturityLevel(MaturityLevelDslModel level, Long kitId) {
        return new MaturityLevel(
            null,
            level.getCode(),
            level.getTitle(),
            level.getIndex(),
            level.getValue(),
            toCompetenceList(level.getCompetencesCodeToValueMap(), kitId)
        );
    }

    private List<MaturityLevelCompetence> toCompetenceList(Map<String, Integer> map, Long kitId) {
        return map.keySet().stream()
            .map(key -> new MaturityLevelCompetence(
                loadMaturityLevelByTitlePort.loadByTitle(key, kitId).getId(),
                map.get(key)))
            .toList();
    }

    private void updateMaturityLevel(List<MaturityLevel> loadedLevels, List<MaturityLevelDslModel> levelModels, Long kitId) {
        for (MaturityLevelDslModel newLevel : levelModels) {
            for (MaturityLevel loadedLevel : loadedLevels) {
                if (newLevel.getCode().equals(loadedLevel.getCode()) && isChanged(newLevel, loadedLevel)) {
                    var updateParam = new UpdateMaturityLevelPort.Param(
                        kitId,
                        newLevel.getCode(),
                        newLevel.getTitle(),
                        newLevel.getIndex(),
                        newLevel.getValue()
                    );
                    updateMaturityLevelPort.update(updateParam);
                    log.warn("A maturity Level with code [{}] is updated.", newLevel.getCode());
                }
            }
        }
    }

    private boolean isChanged(MaturityLevelDslModel newLevel, MaturityLevel loadedLevel) {
        return !newLevel.getTitle().equals(loadedLevel.getTitle()) ||
            newLevel.getValue() != loadedLevel.getValue() ||
            newLevel.getIndex() != loadedLevel.getIndex();
    }

    private void deleteLevelCompetence(Long loadedLevelId, List<MaturityLevelCompetence> loadedCompetences, List<MaturityLevelCompetence> newCompetences, Long kitId) {
        var mustBeDeletedCompetences = loadedCompetences.stream()
            .filter(competence -> newCompetences.stream()
                .noneMatch(newCompetence -> newCompetence.getEffectiveLevelId() == competence.getEffectiveLevelId()))
            .toList();
        mustBeDeletedCompetences.forEach(competence -> deleteLevelCompetence(loadedLevelId, kitId, competence.getEffectiveLevelId()));
    }

    private void createLevelCompetence(String newLevelCode, List<MaturityLevelCompetence> newCompetences, List<MaturityLevelCompetence> loadedCompetences, Long kitId) {
        var newMustBeAddedCompetences = newCompetences.stream()
            .filter(competence -> loadedCompetences.stream()
                .noneMatch(newCompetence -> newCompetence.getEffectiveLevelId() == competence.getEffectiveLevelId()))
            .toList();
        newMustBeAddedCompetences.forEach(competence -> {
            createLevelCompetencePort.persist(
                competence.getEffectiveLevelId(),
                competence.getValue(),
                newLevelCode,
                kitId);
            log.warn("Level Competence with effective level id [{}], maturity level code [{}] and kit id [{}] is created.",
                competence.getEffectiveLevelId(),
                newLevelCode,
                kitId);
        });
    }

    private void updateLevelCompetence(Long loadedLevelId, List<MaturityLevelCompetence> newCompetences, List<MaturityLevelCompetence> loadedCompetences, Long kitId) {
        newCompetences.forEach(newCompetence -> loadedCompetences.forEach(
                loadedCompetence -> {
                    if (newCompetence.getEffectiveLevelId() == loadedCompetence.getEffectiveLevelId() &&
                        newCompetence.getValue() != loadedCompetence.getValue()) {
                        updateLevelCompetencePort.update(
                            loadedLevelId,
                            newCompetence.getEffectiveLevelId(),
                            newCompetence.getValue(),
                            kitId);
                        log.warn("Level Competence with effective level id [{}], maturity level id [{}] and kit id [{}] is updated.",
                            newCompetence.getEffectiveLevelId(),
                            loadedLevelId,
                            kitId);
                    }
                }
            )
        );
    }

}
