package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessment.GenerateAllAssessmentInsightsUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
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
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadSubjectInsightsPort loadSubjectInsightsPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadSubjectValuePort loadSubjectValuePort;
    private final CreateSubjectInsightPort createSubjectInsightPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final CreateAssessmentInsightPort createAssessmentInsightPort;

    @Override
    public void generateAllAssessmentInsights(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var locale = Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode());

        initSubjectsInsight(assessmentResult, locale);
        initAssessmentInsight(assessmentResult, locale);
    }

    private void initSubjectsInsight(AssessmentResult assessmentResult, Locale locale) {
        var subjectIds = loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()).stream()
            .map(Subject::getId)
            .collect(toList());
        var subjectInsightIds = loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()).stream()
            .map(SubjectInsight::getSubjectId)
            .toList();
        subjectIds.removeAll(subjectInsightIds);
        if (!subjectIds.isEmpty())
            createSubjectsInsight(assessmentResult, subjectIds, locale);
    }

    private void createSubjectsInsight(AssessmentResult assessmentResult, List<Long> subjectIds, Locale locale) {
        var maturityLevelsCount = loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()).size();
        var subjectIdToValueMap = loadSubjectValuePort.loadAll(subjectIds, assessmentResult.getId()).stream()
            .collect(toMap(sv -> sv.getSubject().getId(), Function.identity()));
        subjectIds.forEach(subjectId -> {
            var defaultInsight = buildDefaultInsight(
                subjectIdToValueMap.get(subjectId),
                maturityLevelsCount,
                locale);
            var subjectInsight = new SubjectInsight(assessmentResult.getId(),
                subjectId,
                defaultInsight,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                false);
            createSubjectInsightPort.persist(subjectInsight);
        });
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

    private void initAssessmentInsight(AssessmentResult assessmentResult, Locale locale) {
        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId());
        if (assessmentInsight.isEmpty()) {
            createAssessmentInsight(assessmentResult, locale);
        }
    }

    private void createAssessmentInsight(AssessmentResult assessmentResult, Locale locale) {
        var progress = getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId());
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

    AssessmentInsight toAssessmentInsight(UUID assessmentResultId, String insight) {
        return new AssessmentInsight(null,
            assessmentResultId,
            insight,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            false);
    }
}
