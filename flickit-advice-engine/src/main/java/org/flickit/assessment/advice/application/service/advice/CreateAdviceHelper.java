package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.core.api.solver.SolverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.exception.FinalSolutionNotFoundException;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedAttributeIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedLevelIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.advice.application.port.out.calculation.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.calculation.LoadCreatedAdviceDetailsPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.advice.common.ErrorMessageKey.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAdviceHelper {

    private final LoadSelectedAttributeIdsRelatedToAssessmentPort loadSelectedAttributeIdsRelatedToAssessmentPort;
    private final LoadSelectedLevelIdsRelatedToAssessmentPort loadSelectedLevelIdsRelatedToAssessmentPort;
    private final LoadAttributeCurrentAndTargetLevelIndexPort loadAttributeCurrentAndTargetLevelIndexPort;
    private final LoadAdviceCalculationInfoPort loadAdviceCalculationInfoPort;
    private final SolverManager<Plan, UUID> solverManager;
    private final LoadCreatedAdviceDetailsPort loadCreatedAdviceDetailsPort;

    public List<AdviceListItem> createAdvice(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        validateAssessmentAttributeRelation(assessmentId, attributeLevelTargets);
        validateAssessmentLevelRelation(assessmentId, attributeLevelTargets);
        var validAttributeLevelTargets = filterValidAttributeLevelTargets(assessmentId, attributeLevelTargets);

        var problem = loadAdviceCalculationInfoPort.loadAdviceCalculationInfo(assessmentId, validAttributeLevelTargets);
        var solution = solverManager.solve(UUID.randomUUID(), problem);
        Plan plan;
        try {
            plan = solution.getFinalBestSolution();
        } catch (InterruptedException e) {
            log.error("Finding best solution for assessment {} interrupted", assessmentId, e.getCause());
            Thread.currentThread().interrupt();
            throw new FinalSolutionNotFoundException(CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);
        } catch (ExecutionException e) {
            log.error("Error occurred while calculating best solution for assessment {}", assessmentId, e.getCause());
            throw new FinalSolutionNotFoundException(CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);
        }
        return mapToResult(plan, assessmentId);
    }

    private void validateAssessmentAttributeRelation(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        Set<Long> selectedAttrIds = attributeLevelTargets.stream()
            .map(AttributeLevelTarget::getAttributeId)
            .collect(Collectors.toSet());
        Set<Long> loadedAttrIds =
            loadSelectedAttributeIdsRelatedToAssessmentPort.loadSelectedAttributeIdsRelatedToAssessment(assessmentId, selectedAttrIds);
        if (loadedAttrIds.size() != selectedAttrIds.size())
            throw new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_ATTRIBUTE_RELATION_NOT_FOUND);
    }

    private void validateAssessmentLevelRelation(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        Set<Long> selectedLevelIds = attributeLevelTargets.stream()
            .map(AttributeLevelTarget::getMaturityLevelId)
            .collect(Collectors.toSet());
        Set<Long> loadedLevelIds =
            loadSelectedLevelIdsRelatedToAssessmentPort.loadSelectedLevelIdsRelatedToAssessment(assessmentId, selectedLevelIds);
        if (loadedLevelIds.size() != selectedLevelIds.size())
            throw new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_LEVEL_RELATION_NOT_FOUND);
    }

    private List<AttributeLevelTarget> filterValidAttributeLevelTargets(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        var attributeCurrentAndTargetLevelIndexes = loadAttributeCurrentAndTargetLevelIndexPort.load(assessmentId, attributeLevelTargets);
        var validAttributeIds = attributeCurrentAndTargetLevelIndexes.stream()
            .filter(a -> a.targetMaturityLevelIndex() > a.currentMaturityLevelIndex())
            .map(LoadAttributeCurrentAndTargetLevelIndexPort.Result::attributeId)
            .collect(Collectors.toSet());
        if (validAttributeIds.isEmpty())
            throw new ValidationException(CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN);

        return attributeLevelTargets.stream()
            .filter(a -> validAttributeIds.contains(a.getAttributeId()))
            .toList();
    }

    private List<AdviceListItem> mapToResult(Plan solution, UUID assessmentId) {
        var questionIdsMap = solution.getQuestions().stream()
            .filter(Question::isRecommended)
            .collect(Collectors.toMap(Question::getId, Function.identity()));

        var adviceQuestionDetails = loadCreatedAdviceDetailsPort.loadAdviceDetails(questionIdsMap.keySet().stream().toList(), assessmentId);

        return adviceQuestionDetails.stream().map(adv -> {
                var question = questionIdsMap.get(adv.question().id());
                var answeredOption = question.getCurrentOptionIndex() != null ? adv.options().get(question.getCurrentOptionIndex()) : null;
                var recommendedOption = adv.options().get(question.getRecommendedOptionIndex());
                var benefit = question.calculateBenefit();

                return new AdviceListItem(
                    adv.question(),
                    answeredOption,
                    recommendedOption,
                    benefit,
                    adv.attributes(),
                    adv.questionnaire());
            }).sorted(Comparator.comparingDouble(AdviceListItem::benefit).reversed())
            .toList();
    }
}
