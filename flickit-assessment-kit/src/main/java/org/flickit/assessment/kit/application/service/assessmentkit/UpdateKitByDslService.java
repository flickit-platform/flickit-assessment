package org.flickit.assessment.kit.application.service.assessmentkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.flickit.assessment.kit.application.exception.NotValidKitContentException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.CreateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_VALID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateKitByDslService implements UpdateKitByDslUseCase {

    private final LoadAssessmentKitInfoPort loadAssessmentKitInfoPort;
    private final CreateMaturityLevelPort createMaturityLevelPort;
    private final DeleteMaturityLevelPort deleteMaturityLevelPort;
    private final UpdateMaturityLevelPort updateMaturityLevelPort;
    private final DeleteLevelCompetencePort deleteLevelCompetencePort;
    private final CreateLevelCompetencePort createLevelCompetencePort;
    private final UpdateLevelCompetencePort updateLevelCompetencePort;

    @Override
    public void update(Param param) {
        AssessmentKit loadedKit = loadAssessmentKitInfoPort.load(param.getKitId());
        AssessmentKitDslModel kitModel = parseJson(param.getDslContent());

        if (kitModel != null) {
            checkLevel(param.getKitId(), loadedKit.getMaturityLevels(), kitModel.getMaturityLevels());
        }
    }

    private AssessmentKitDslModel parseJson(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, AssessmentKitDslModel.class);
        } catch (JsonProcessingException e) {
            throw new NotValidKitContentException(UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_VALID);
        }
    }

    private void checkLevel(Long kitId, List<MaturityLevel> loadedLevels, List<MaturityLevelDslModel> levelModels) {
        deleteMaturityLevel(loadedLevels, levelModels, kitId);
        createMaturityLevel(kitId, loadedLevels, levelModels);

        for (MaturityLevelDslModel newLevel : levelModels) {
            for (MaturityLevel loadedLevel : loadedLevels) {
                if (newLevel.getCode().equals(loadedLevel.getCode())) {
//                    updateMaturityLevel(newLevel, loadedLevel);

                    Map<String, Integer> newCompetences = newLevel.getCompetencesCodeToValueMap();
                    Map<String, Integer> loadedCompetences = loadedLevel.getLevelCompetence();
                    deleteLevelCompetence(loadedLevel.getId(), loadedCompetences, newCompetences, kitId);
                    if (newCompetences != null) {
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
            level.getLevelCompetence().keySet()
                    .forEach(levelCompetence -> deleteLevelCompetence(level.getId(), kitId, levelCompetence));
            deleteMaturityLevelPort.delete(level.getId());
            log.warn("Maturity Level with id [{}] and title [{}] is deleted.", level.getId(), level.getTitle());
        });
    }

    private void createMaturityLevel(Long kitId, List<MaturityLevel> loadedLevels, List<MaturityLevelDslModel> levelModels) {
        List<MaturityLevelDslModel> newLevels = levelModels.stream()
            .filter(level -> loadedLevels.stream()
                .noneMatch(loadedLevel -> loadedLevel.getCode().equals(level.getCode())))
            .toList();
        newLevels.forEach(level -> {
            createMaturityLevelPort.persist(toMaturityLevel(level), kitId);
            log.warn("Maturity Level with title [{}] and kit id [{}] is created.", level.getTitle(), kitId);
        });
    }

    private MaturityLevel toMaturityLevel(MaturityLevelDslModel level) {
        return new MaturityLevel(
            null,
            level.getCode(),
            level.getTitle(),
            level.getDescription(),
            level.getIndex(),
            level.getCompetencesCodeToValueMap(),
            level.getValue()
        );
    }

    private void updateMaturityLevel(MaturityLevelDslModel newLevel, MaturityLevel loadedLevel) {
        if (isChanged(newLevel, loadedLevel)) {
            var updateParam = new UpdateMaturityLevelPort.Param(
                newLevel.getCode(),
                newLevel.getTitle(),
                newLevel.getIndex(),
                newLevel.getValue()
            );
            updateMaturityLevelPort.update(updateParam);
            log.warn("Maturity Level with title [{}] is updated.", newLevel.getTitle());
        }
    }

    private boolean isChanged(MaturityLevelDslModel newLevel, MaturityLevel loadedLevel) {
        return !newLevel.getTitle().equals(loadedLevel.getTitle())
            || newLevel.getValue() != loadedLevel.getValue()
            || newLevel.getIndex() != loadedLevel.getIndex();
    }

    private void deleteLevelCompetence(Long loadedLevelId, Map<String, Integer> loadedCompetences, Map<String, Integer> newCompetences, Long kitId) {
        List<String> mustBeDeletedCompetences = loadedCompetences.keySet().stream()
            .filter(competence -> newCompetences.keySet().stream()
                .noneMatch(newCompetence -> newCompetence.equals(competence)))
            .toList();
        mustBeDeletedCompetences.forEach(competenceLevelTitle -> {
            deleteLevelCompetence(loadedLevelId, kitId, competenceLevelTitle);
        });
    }

    private void deleteLevelCompetence(Long loadedLevelId, Long kitId, String competenceLevelTitle) {
        deleteLevelCompetencePort.delete(competenceLevelTitle, loadedLevelId, kitId);
        log.warn("Level Competence with level competence title [{}], maturity level id [{}] and kit id [{}] is deleted.",
            competenceLevelTitle,
            loadedLevelId,
            kitId);
    }

    private void createLevelCompetence(String newLevelCode, Map<String, Integer> newCompetences, Map<String, Integer> loadedCompetences, Long kitId) {
        List<String> newMustBeAddedCompetences = newCompetences.keySet().stream()
            .filter(competence -> loadedCompetences.keySet().stream()
                .noneMatch(newCompetence -> newCompetence.equals(competence)))
            .toList();
        newMustBeAddedCompetences.forEach(maturityLevelTitle -> {
            createLevelCompetencePort.persist(maturityLevelTitle,
                newCompetences.get(maturityLevelTitle),
                newLevelCode,
                kitId);
            log.warn("Level Competence with level competence title [{}], maturity level id [{}] and kit id [{}] is created.",
                maturityLevelTitle,
                newLevelCode,
                kitId);
        });
    }

    private void updateLevelCompetence(Long loadedLevelId, Map<String, Integer> newCompetences, Map<String, Integer> loadedCompetences, Long kitId) {
        newCompetences.keySet().forEach(
            newCompetence ->
                loadedCompetences.keySet().forEach(
                    loadedCompetence -> {
                        if (newCompetence.equals(loadedCompetence) &&
                            !newCompetences.get(newCompetence).equals(loadedCompetences.get(loadedCompetence))) {
                            updateLevelCompetencePort.update(loadedLevelId, newCompetence, newCompetences.get(newCompetence), kitId);
                            log.warn("Level Competence with level competence title [{}], maturity level id [{}] and kit id [{}] is updated.",
                                newCompetence,
                                loadedLevelId,
                                kitId);
                        }
                    }
                )
        );
    }

}
