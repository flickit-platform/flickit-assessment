package org.flickit.assessment.core.application.service.insight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.insight.GenerateAllAssessmentInsightsUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.subject.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GENERATE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.MessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class GenerateAllAssessmentInsightsService implements GenerateAllAssessmentInsightsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final LoadAttributesPort loadAttributesPort;
    private final LoadAttributeInsightsPort loadAttributeInsightsPort;
    private final CreateAttributeAiInsightHelper createAttributeAiInsightHelper;
    private final CreateAttributeInsightPort createAttributeInsightPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadSubjectInsightsPort loadSubjectInsightsPort;
    private final LoadSubjectValuePort loadSubjectValuePort;
    private final CreateSubjectInsightPort createSubjectInsightPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final CreateAssessmentInsightPort createAssessmentInsightPort;

    @Override
    public void generateAllAssessmentInsights(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var locale = Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode());
        var maturityLevels = loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId());
        var progress = getAssessmentProgressPort.getProgress(param.getAssessmentId());

        initAttributesInsight(param.getAssessmentId(), assessmentResult, maturityLevels, progress, locale);
        initSubjectsInsight(assessmentResult, maturityLevels.size(), locale);
        initAssessmentInsight(assessmentResult, progress, locale);
    }

    private void initAttributesInsight(UUID assessmentId,
                                       AssessmentResult assessmentResult,
                                       List<MaturityLevel> maturityLevels,
                                       GetAssessmentProgressPort.Result progress,
                                       Locale locale) {
        var attributeIds = loadAttributesPort.loadAll(assessmentId).stream()
            .map(LoadAttributesPort.Result::id)
            .collect(toList());
        var attributeInsightIds = loadAttributeInsightsPort.loadInsights(assessmentResult.getId()).stream()
            .map(AttributeInsight::getAttributeId)
            .toList();
        attributeIds.removeAll(attributeInsightIds);
        if (!attributeIds.isEmpty())
            createAttributesInsight(assessmentResult, attributeIds, maturityLevels, progress, locale);
    }

    private void createAttributesInsight(AssessmentResult assessmentResult,
                                         List<Long> attributeIds,
                                         List<MaturityLevel> maturityLevels,
                                         GetAssessmentProgressPort.Result progress,
                                         Locale locale) {
        var attributeInsights = attributeIds.stream()
            .map(id -> {
                var createAiInsightParam = new CreateAttributeAiInsightHelper.Param(assessmentResult,
                    id,
                    maturityLevels,
                    progress,
                    locale);
                return createAttributeAiInsightHelper.createAttributeAiInsight(createAiInsightParam);
            })
            .toList();
        createAttributeInsightPort.persistAll(attributeInsights);
    }

    private void initSubjectsInsight(AssessmentResult assessmentResult, int maturityLevelsCount, Locale locale) {
        var subjectIds = loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()).stream()
            .map(Subject::getId)
            .collect(toList());
        var subjectInsightIds = loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()).stream()
            .map(SubjectInsight::getSubjectId)
            .toList();
        subjectIds.removeAll(subjectInsightIds);
        if (!subjectIds.isEmpty())
            createSubjectsInsight(assessmentResult, subjectIds, maturityLevelsCount, locale);
    }

    private void createSubjectsInsight(AssessmentResult assessmentResult,
                                       List<Long> subjectIds,
                                       int maturityLevelsCount,
                                       Locale locale) {
        var subjectIdToValueMap = loadSubjectValuePort.loadAll(assessmentResult.getId(), subjectIds).stream()
            .collect(toMap(sv -> sv.getSubject().getId(), Function.identity()));
        var subjectInsights = subjectIds.stream()
            .map(subjectId -> {
                var insight = buildDefaultInsight(subjectIdToValueMap.get(subjectId), maturityLevelsCount, locale);
                return new SubjectInsight(assessmentResult.getId(),
                    subjectId,
                    insight,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null,
                    false);
            })
            .toList();
        createSubjectInsightPort.persistAll(subjectInsights);
    }

    private String buildDefaultInsight(SubjectValue subjectValue, int maturityLevelsCount, Locale locale) {
        return MessageBundle.message(SUBJECT_DEFAULT_INSIGHT,
            locale,
            subjectValue.getSubject().getTitle(),
            subjectValue.getSubject().getDescription(),
            subjectValue.getConfidenceValue() != null ? (int) Math.ceil(subjectValue.getConfidenceValue()) : 0,
            subjectValue.getSubject().getTitle(),
            subjectValue.getMaturityLevel().getIndex(),
            maturityLevelsCount,
            subjectValue.getMaturityLevel().getTitle(),
            subjectValue.getSubject().getAttributes().size(),
            subjectValue.getSubject().getTitle());
    }

    private void initAssessmentInsight(AssessmentResult assessmentResult,
                                       GetAssessmentProgressPort.Result progress,
                                       Locale locale) {
        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId());
        if (assessmentInsight.isEmpty())
            createAssessmentInsight(assessmentResult, progress, locale);
    }

    private void createAssessmentInsight(AssessmentResult assessmentResult,
                                         GetAssessmentProgressPort.Result progress,
                                         Locale locale) {
        var questionsCount = progress.questionsCount();
        var answersCount = progress.answersCount();
        var confidenceValue = assessmentResult.getConfidenceValue() != null
            ? (int) Math.ceil(assessmentResult.getConfidenceValue())
            : 0;
        var maturityLevelTitle = assessmentResult.getMaturityLevel().getTitle();
        var insight = (questionsCount == answersCount)
            ? MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED,
            locale,
            maturityLevelTitle,
            questionsCount,
            confidenceValue)
            : MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE,
            locale,
            maturityLevelTitle,
            answersCount,
            questionsCount,
            confidenceValue);
        createAssessmentInsightPort.persist(toAssessmentInsight(assessmentResult.getId(), insight));
    }

    private AssessmentInsight toAssessmentInsight(UUID assessmentResultId, String insight) {
        return new AssessmentInsight(null,
            assessmentResultId,
            insight,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            false);
    }
}
