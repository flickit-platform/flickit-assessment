package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessment.GenerateAllAssessmentInsightsUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeScoresFilePort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoresFilePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GENERATE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GENERATE_ALL_ASSESSMENT_INSIGHTS_ALL_QUESTIONS_NOT_ANSWERED;
import static org.flickit.assessment.core.common.MessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class GenerateAllAssessmentInsightsService implements GenerateAllAssessmentInsightsUseCase {

    private static final String AI_INPUT_FILE_EXTENSION = ".xlsx";

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final LoadAttributesPort loadAttributesPort;
    private final LoadAttributeInsightsPort loadAttributeInsightsPort;
    private final AppAiProperties appAiProperties;
    private final LoadAttributeValuePort loadAttributeValuePort;
    private final CreateAttributeScoresFilePort createAttributeScoresFilePort;
    private final CallAiPromptPort callAiPromptPort;
    private final UploadAttributeScoresFilePort uploadAttributeScoresFilePort;
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
        if (progress.answersCount() != progress.questionsCount())
            throw new ValidationException(GENERATE_ALL_ASSESSMENT_INSIGHTS_ALL_QUESTIONS_NOT_ANSWERED);
        if (!appAiProperties.isEnabled())
            throw new UnsupportedOperationException(ASSESSMENT_AI_IS_DISABLED);
        var assessment = assessmentResult.getAssessment();
        var assessmentTitle = getAssessmentTitle(assessment);
        var promptTemplate = appAiProperties.getPrompt().getAttributeInsight();
        var attributeInsights = attributeIds.stream()
            .map(attributeId -> {
                var attributeValue = loadAttributeValuePort.load(assessmentResult.getId(), attributeId);
                var file = createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels);
                var prompt = createPrompt(promptTemplate,
                    attributeValue.getAttribute().getTitle(),
                    attributeValue.getAttribute().getDescription(),
                    assessmentTitle,
                    file.text(),
                    locale.getDisplayLanguage());
                var aiInsight = callAiPromptPort.call(prompt, AiResponseDto.class).value();
                var aiInputPath = uploadInputFile(attributeValue.getAttribute(), file.stream());
                return toAttributeInsight(assessmentResult.getId(),
                    attributeValue.getAttribute().getId(),
                    aiInsight,
                    aiInputPath);
            })
            .toList();
        createAttributeInsightPort.persistAll(attributeInsights);
    }

    private String getAssessmentTitle(Assessment assessment) {
        return assessment.getShortTitle() != null
            ? assessment.getShortTitle()
            : assessment.getTitle();
    }

    private Prompt createPrompt(String promptTemplate,
                                String attributeTitle,
                                String attributeDescription,
                                String assessmentTitle,
                                String fileContent,
                                String language) {
        return new PromptTemplate(promptTemplate,
            Map.of("attributeTitle", attributeTitle,
                "attributeDescription", attributeDescription,
                "assessmentTitle", assessmentTitle,
                "fileContent", fileContent,
                "language", language))
            .create();
    }

    record AiResponseDto(String value) {
    }

    private String uploadInputFile(Attribute attribute, InputStream stream) {
        String aiInputPath = null;
        if (appAiProperties.isSaveAiInputFileEnabled()) {
            var fileName = attribute.getTitle() + AI_INPUT_FILE_EXTENSION;
            aiInputPath = uploadAttributeScoresFilePort.uploadExcel(stream, fileName);
        }
        return aiInputPath;
    }

    private AttributeInsight toAttributeInsight(UUID assessmentResultId,
                                                long attributeId,
                                                String aiInsight,
                                                String aiInputPath) {
        return new AttributeInsight(assessmentResultId,
            attributeId,
            aiInsight,
            null,
            LocalDateTime.now(),
            null,
            aiInputPath,
            false,
            LocalDateTime.now());
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
