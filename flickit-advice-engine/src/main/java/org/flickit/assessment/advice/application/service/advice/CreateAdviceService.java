package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.core.api.solver.SolverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.exception.FinalSolutionNotFoundException;
import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentKitVersionIdPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedAttributeIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedLevelIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.advice.application.port.out.calculation.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.calculation.LoadCreatedAdviceDetailsPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
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
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAdviceService implements CreateAdviceUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadSelectedAttributeIdsRelatedToAssessmentPort loadSelectedAttributeIdsRelatedToAssessmentPort;
    private final LoadSelectedLevelIdsRelatedToAssessmentPort loadSelectedLevelIdsRelatedToAssessmentPort;
    private final LoadAttributeCurrentAndTargetLevelIndexPort loadAttributeCurrentAndTargetLevelIndexPort;
    private final LoadAdviceCalculationInfoPort loadAdviceCalculationInfoPort;
    private final SolverManager<Plan, UUID> solverManager;
    private final LoadAssessmentKitVersionIdPort loadAssessmentKitVersionIdPort;
    private final LoadCreatedAdviceDetailsPort loadCreatedAdviceDetailsPort;

    @Override
    public Result createAdvice(Param param) {
        UUID assessmentId = param.getAssessmentId();

        validateUserAccess(assessmentId, param.getCurrentUserId());

        validateAssessmentResultPort.validate(assessmentId);

        List<AttributeLevelTarget> attributeLevelTargets = param.getAttributeLevelTargets();
        validateAssessmentAttributeRelation(assessmentId, attributeLevelTargets);
        validateAssessmentLevelRelation(assessmentId, attributeLevelTargets);
        var validAttributeLevelTargets = filterValidAttributeLevelTargets(param.getAssessmentId(), param.getAttributeLevelTargets());

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
        var kitVersionId = loadAssessmentKitVersionIdPort.loadKitVersionIdById(assessmentId);
        return mapToResult(plan, kitVersionId);
    }

    private void validateUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, CREATE_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
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
        var attributeCurrentAndTargetLevelIndexes = loadAttributeCurrentAndTargetLevelIndexPort.loadAttributeCurrentAndTargetLevelIndex(assessmentId, attributeLevelTargets);
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

    private Result mapToResult(Plan solution, Long kitVersionId) {
        var questionIdsMap = solution.getQuestions().stream()
            .filter(Question::isRecommended)
            .collect(Collectors.toMap(Question::getId, Function.identity()));

        var adviceQuestionDetails = loadCreatedAdviceDetailsPort.loadAdviceDetails(questionIdsMap.keySet().stream().toList(), kitVersionId);

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
