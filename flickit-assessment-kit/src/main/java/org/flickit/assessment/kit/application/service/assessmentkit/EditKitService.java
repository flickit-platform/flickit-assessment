package org.flickit.assessment.kit.application.service.assessmentkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
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

@Service
@Transactional
@RequiredArgsConstructor
public class EditKitService implements UpdateKitByDslUseCase {

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
        AssessmentKit kitModel = parseJson(param.getDslContent());

        if (kitModel != null) {
            checkLevel(param.getKitId(), loadedKit.getLevels(), kitModel.getLevels());
        }
    }

    private AssessmentKit parseJson(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, AssessmentKit.class);
        } catch (JsonProcessingException e) {
            throw new NotValidKitContentException(UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_VALID);
        }
    }

    private void checkLevel(Long kitId, List<MaturityLevel> loadedLevels, List<MaturityLevel> levelModels) {
        deleteMaturityLevel(loadedLevels, levelModels);
        createMaturityLevel(kitId, loadedLevels, levelModels);

        for (MaturityLevel newLevel : levelModels) {
            for (MaturityLevel loadedLevel : loadedLevels) {
                if (newLevel.getCode().equals(loadedLevel.getCode())) {
                    updateMaturityLevel(newLevel, loadedLevel);

                    Map<String, Integer> newCompetences = newLevel.getLevelCompetence();
                    Map<String, Integer> loadedCompetences = loadedLevel.getLevelCompetence();
                    deleteLevelCompetence(loadedLevel.getId(), loadedCompetences, newCompetences);
                    createLevelCompetence(newLevel.getCode(), newCompetences, loadedCompetences);
                    updateLevelCompetence(loadedLevel.getId(), newCompetences, loadedCompetences);
                }
            }
        }

    }

    private void deleteMaturityLevel(List<MaturityLevel> loadedLevels, List<MaturityLevel> levelModels) {
        List<MaturityLevel> mustBeDeletedLevels = loadedLevels.stream()
            .filter(level -> levelModels.stream()
                .noneMatch(levelModel -> levelModel.getCode().equals(level.getCode())))
            .toList();
        mustBeDeletedLevels.forEach(level -> deleteMaturityLevelPort.delete(level.getId()));
    }

    private void createMaturityLevel(Long kitId, List<MaturityLevel> loadedLevels, List<MaturityLevel> levelModels) {
        List<MaturityLevel> newLevels = levelModels.stream()
            .filter(level -> loadedLevels.stream()
                .noneMatch(loadedLevel -> loadedLevel.getCode().equals(level.getCode())))
            .toList();
        newLevels.forEach(level -> createMaturityLevelPort.persist(level, kitId));
    }

    private void updateMaturityLevel(MaturityLevel newLevel, MaturityLevel loadedLevel) {
        if (isChanged(newLevel, loadedLevel)) {
            var updateParam = new UpdateMaturityLevelPort.Param(
                newLevel.getCode(),
                newLevel.getTitle(),
                newLevel.getIndex(),
                newLevel.getValue()
            );
            updateMaturityLevelPort.update(updateParam);
        }
    }

    private void deleteLevelCompetence(Long loadedLevelId, Map<String, Integer> loadedCompetences, Map<String, Integer> newCompetences) {
        List<String> mustBeDeletedCompetences = loadedCompetences.keySet().stream()
            .filter(competence -> newCompetences.keySet().stream()
                .noneMatch(newCompetence -> newCompetence.equals(competence)))
            .toList();
        mustBeDeletedCompetences.forEach(competenceLevelTitle ->
            deleteLevelCompetencePort.delete(competenceLevelTitle, loadedLevelId));
    }

    private void createLevelCompetence(String newLevelCode, Map<String, Integer> newCompetences, Map<String, Integer> loadedCompetences) {
        List<String> newMustBeAddedCompetences = newCompetences.keySet().stream()
            .filter(competence -> loadedCompetences.keySet().stream()
                .noneMatch(newCompetence -> newCompetence.equals(competence)))
            .toList();
        newMustBeAddedCompetences.forEach(maturityLevelTitle ->
            createLevelCompetencePort.persist(maturityLevelTitle,
                newCompetences.get(maturityLevelTitle),
                newLevelCode));
    }

    private void updateLevelCompetence(Long loadedLevelId, Map<String, Integer> newCompetences, Map<String, Integer> loadedCompetences) {
        newCompetences.keySet().forEach(
            newCompetence -> {
                loadedCompetences.keySet().forEach(
                    loadedCompetence -> {
                        if (newCompetence.equals(loadedCompetence) &&
                            !newCompetences.get(newCompetence).equals(loadedCompetences.get(loadedCompetence))) {
                            updateLevelCompetencePort.update(loadedLevelId, newCompetence, newCompetences.get(newCompetence));
                        }
                    }
                );
            }
        );
    }

    private boolean isChanged(MaturityLevel newLevel, MaturityLevel loadedLevel) {
        return !newLevel.getTitle().equals(loadedLevel.getTitle())
            || newLevel.getValue() != loadedLevel.getValue()
            || newLevel.getIndex() != loadedLevel.getIndex();
    }

}
