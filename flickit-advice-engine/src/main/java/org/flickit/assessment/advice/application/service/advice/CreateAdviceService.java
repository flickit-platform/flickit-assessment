package org.flickit.assessment.advice.application.service.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.port.in.advice.CreateAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.advicequestion.CreateAdviceQuestionPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentSpacePort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedAttributeIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedLevelIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessmentadvice.CreateAdvicePort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultIdPort;
import org.flickit.assessment.advice.application.port.out.attributeleveltarget.CreateAttributeLevelTargetPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.advice.application.port.out.space.CheckSpaceAccessPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateAdviceService implements CreateAdviceUseCase {

    private final LoadAssessmentSpacePort loadAssessmentSpacePort;
    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final LoadSelectedAttributeIdsRelatedToAssessmentPort loadSelectedAttributeIdsRelatedToAssessmentPort;
    private final LoadSelectedLevelIdsRelatedToAssessmentPort loadSelectedLevelIdsRelatedToAssessmentPort;
    private final LoadAttributeCurrentAndTargetLevelIndexPort loadAttributeCurrentAndTargetLevelIndexPort;
    private final LoadAssessmentResultIdPort loadAssessmentResultIdPort;
    private final CreateAdvicePort createAdvicePort;
    private final CreateAttributeLevelTargetPort createAttributeLevelTargetPort;
    private final CreateAdviceQuestionPort createAdviceQuestionPort;

    @Override
    public Result createAdvice(Param param) {
        validateParamBeforeCreate(param);
        var validAttributeLevelTargets = filterValidAttributeLevelTargets(param.getAssessmentId(), param.getAttributeLevelTargets());
        UUID adviceId = create(param, validAttributeLevelTargets);
        return new Result(adviceId);
    }

    private void validateParamBeforeCreate(Param param) {
        validateUserAccess(param.getAssessmentId(), param.getCurrentUserId());
        validateAssessmentAttributeRelation(param.getAssessmentId(), param.getAttributeLevelTargets());
        validateAssessmentLevelRelation(param.getAssessmentId(), param.getAttributeLevelTargets());
    }

    private void validateUserAccess(UUID assessmentId, UUID currentUserId) {
        var spaceId = loadAssessmentSpacePort.loadAssessmentSpaceId(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_NOT_FOUND));
        if (!checkSpaceAccessPort.checkIsMember(spaceId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private void validateAssessmentAttributeRelation(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        Set<Long> selectedAttrIds = attributeLevelTargets.stream()
            .map(AttributeLevelTarget::getAttributeId)
            .collect(Collectors.toSet());
        Set<Long> loadedAttrIds =
            loadSelectedAttributeIdsRelatedToAssessmentPort.loadSelectedAttributeIdsRelatedToAssessment(assessmentId, selectedAttrIds);
        if (loadedAttrIds.size() != selectedAttrIds.size()) {
            throw new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_ATTRIBUTE_RELATION_NOT_FOUND);
        }
    }

    private void validateAssessmentLevelRelation(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        Set<Long> selectedLevelIds = attributeLevelTargets.stream()
            .map(AttributeLevelTarget::getMaturityLevelId)
            .collect(Collectors.toSet());
        Set<Long> loadedLevelIds =
            loadSelectedLevelIdsRelatedToAssessmentPort.loadSelectedLevelIdsRelatedToAssessment(assessmentId, selectedLevelIds);
        if (loadedLevelIds.size() != selectedLevelIds.size()) {
            throw new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_LEVEL_RELATION_NOT_FOUND);
        }
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

    private UUID create(Param param, List<AttributeLevelTarget> validAttributeLevelTargets) {
        var assessmentResultId = loadAssessmentResultIdPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND));
        var adviceId = createAdvicePort.persist(toCreateAdviceParam(param, assessmentResultId));
        createAttributeLevelTargetPort.persistAll(adviceId, validAttributeLevelTargets);
        createAdviceQuestionPort.persistAll(adviceId, param.getAdviceQuestions());
        return adviceId;
    }

    private CreateAdvicePort.Param toCreateAdviceParam(Param param, UUID assessmentResultId) {
        return new CreateAdvicePort.Param(
            assessmentResultId,
            param.getCurrentUserId(),
            LocalDateTime.now(),
            LocalDateTime.now());
    }
}
