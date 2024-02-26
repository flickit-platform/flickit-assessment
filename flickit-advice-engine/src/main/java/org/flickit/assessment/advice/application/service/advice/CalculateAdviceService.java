package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.core.api.solver.SolverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.exception.FinalSolutionNotFoundException;
import org.flickit.assessment.advice.application.port.in.advice.CalculateAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentSpacePort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedAttributeIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedLevelIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.advice.application.port.out.calculation.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.calculation.LoadCalculatedAdviceDetailsPort;
import org.flickit.assessment.advice.application.port.out.space.CheckSpaceAccessPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
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
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateAdviceService implements CalculateAdviceUseCase {

    private final LoadAssessmentSpacePort loadAssessmentSpacePort;
    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAttributeCurrentAndTargetLevelIndexPort loadAttributeCurrentAndTargetLevelIndexPort;
    private final LoadAdviceCalculationInfoPort loadAdviceCalculationInfoPort;
    private final SolverManager<Plan, UUID> solverManager;
    private final LoadCalculatedAdviceDetailsPort loadCalculatedAdviceDetailsPort;
    private final LoadSelectedAttributeIdsRelatedToAssessmentPort loadSelectedAttributeIdsRelatedToAssessmentPort;
    private final LoadSelectedLevelIdsRelatedToAssessmentPort loadSelectedLevelIdsRelatedToAssessmentPort;

    @Override
    public Result calculateAdvice(Param param) {
        UUID assessmentId = param.getAssessmentId();

        validateUserAccess(assessmentId, param.getCurrentUserId());

        validateAssessmentResultPort.validate(assessmentId);

        List<AttributeLevelTarget> attributeLevelTargets = param.getAttributeLevelTargets();
        validateAssessmentAttributeRelation(assessmentId, attributeLevelTargets);
        validateAssessmentLevelRelation(assessmentId, attributeLevelTargets);
        var validAttributeLevelTargets = filterValidAttributeLevelTargets(param.getAssessmentId(), param.getAttributeLevelTargets());

        var problem = loadAdviceCalculationInfoPort.loadAdviceCalculationInfo(assessmentId, validAttributeLevelTargets);
        var solver = solverManager.solve(UUID.randomUUID(), problem);
        Plan solution;
        try {
            solution = solver.getFinalBestSolution();
        } catch (InterruptedException e) {
            log.error("Finding best solution for assessment {} interrupted", assessmentId, e.getCause());
            Thread.currentThread().interrupt();
            throw new FinalSolutionNotFoundException(CALCULATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);
        } catch (ExecutionException e) {
            log.error("Error occurred while calculating best solution for assessment {}", assessmentId, e.getCause());
            throw new FinalSolutionNotFoundException(CALCULATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);
        }
        return mapToResult(solution);
    }

    private void validateUserAccess(UUID assessmentId, UUID currentUserId) {
        var spaceId = loadAssessmentSpacePort.loadAssessmentSpaceId(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(CALCULATE_ADVICE_ASSESSMENT_NOT_FOUND));

        if (!checkSpaceAccessPort.checkIsMember(spaceId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private void validateAssessmentAttributeRelation(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        Set<Long> selectedAttrIds = attributeLevelTargets.stream()
            .map(AttributeLevelTarget::getAttributeId)
            .collect(Collectors.toSet());
        Set<Long> loadedAttrIds =
            loadSelectedAttributeIdsRelatedToAssessmentPort.loadSelectedAttributeIdsRelatedToAssessment(assessmentId, selectedAttrIds);
        if (loadedAttrIds.size() != selectedAttrIds.size())
            throw new ResourceNotFoundException(CALCULATE_ADVICE_ASSESSMENT_ATTRIBUTE_RELATION_NOT_FOUND);
    }

    private void validateAssessmentLevelRelation(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        Set<Long> selectedLevelIds = attributeLevelTargets.stream()
            .map(AttributeLevelTarget::getMaturityLevelId)
            .collect(Collectors.toSet());
        Set<Long> loadedLevelIds =
            loadSelectedLevelIdsRelatedToAssessmentPort.loadSelectedLevelIdsRelatedToAssessment(assessmentId, selectedLevelIds);
        if (loadedLevelIds.size() != selectedLevelIds.size())
            throw new ResourceNotFoundException(CALCULATE_ADVICE_ASSESSMENT_LEVEL_RELATION_NOT_FOUND);
    }

    private List<AttributeLevelTarget> filterValidAttributeLevelTargets(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        var attributeCurrentAndTargetLevelIndexes = loadAttributeCurrentAndTargetLevelIndexPort.loadAttributeCurrentAndTargetLevelIndex(assessmentId, attributeLevelTargets);
        var validAttributeIds = attributeCurrentAndTargetLevelIndexes.stream()
            .filter(a -> a.targetMaturityLevelIndex() > a.currentMaturityLevelIndex())
            .map(LoadAttributeCurrentAndTargetLevelIndexPort.Result::attributeId)
            .collect(Collectors.toSet());
        if (validAttributeIds.isEmpty())
            throw new ValidationException(CALCULATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN);

        return attributeLevelTargets.stream()
            .filter(a -> validAttributeIds.contains(a.getAttributeId()))
            .toList();
    }

    private Result mapToResult(Plan solution) {
        var questionIdsMap = solution.getQuestions().stream()
            .filter(Question::isRecommended)
            .collect(Collectors.toMap(Question::getId, Function.identity()));

        var adviceQuestionDetails = loadCalculatedAdviceDetailsPort.loadAdviceDetails(questionIdsMap.keySet().stream().toList());

        var adviceListItems = adviceQuestionDetails.stream().map(adv -> {
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

        return new Result(adviceListItems);
    }
}
