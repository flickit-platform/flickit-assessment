package org.flickit.assessment.core.application.service.insight.attribute;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.insight.AttributeInsight;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeScoresFilePort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoresFilePort;
import org.jetbrains.annotations.Nullable;
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

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_AI_IS_DISABLED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAttributeAiInsightHelper {

    private static final String AI_INPUT_FILE_EXTENSION = ".xlsx";

    private final LoadAttributeValuePort loadAttributeValuePort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final AppAiProperties appAiProperties;
    private final CreateAttributeScoresFilePort createAttributeScoresFilePort;
    private final UploadAttributeScoresFilePort uploadAttributeScoresFilePort;
    private final CallAiPromptPort callAiPromptPort;

    @SneakyThrows
    public AttributeInsight createAttributeAiInsight(AttributeInsightParam param) {
        var assessment = param.assessmentResult().getAssessment();
        var assessmentProgress = getAssessmentProgressPort.getProgress(assessment.getId());
        if (assessmentProgress.answersCount() != assessmentProgress.questionsCount())
            throw new ValidationException(CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED);

        var attributeValue = loadAttributeValuePort.load(param.assessmentResult().getId(), param.attributeId());
        var attribute = attributeValue.getAttribute();

        if (!appAiProperties.isEnabled())
            throw new UnsupportedOperationException(ASSESSMENT_AI_IS_DISABLED);

        var assessmentTitle = getAssessmentTitle(assessment);
        var maturityLevels = loadMaturityLevelsPort.loadByAssessmentId(param.assessmentResult().getAssessment().getId());
        var file = createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels);
        var prompt = createPrompt(attribute.getTitle(),
            attribute.getDescription(),
            assessmentTitle,
            file.text(),
            param.locale().getDisplayLanguage());
        var aiInsight = callAiPromptPort.call(prompt, AiResponseDto.class).value();
        var aiInputPath = uploadInputFile(attribute, file.stream());

        return toAttributeInsight(param.assessmentResult().getId(), attribute.getId(), aiInsight, aiInputPath);
    }

    @Builder
    public record AttributeInsightParam(AssessmentResult assessmentResult,
                                        Long attributeId,
                                        Locale locale) {
    }

    @SneakyThrows
    public List<AttributeInsight> createAttributeAiInsights(AttributeInsightsParam param) {
        var assessment = param.assessmentResult().getAssessment();
        var assessmentProgress = getAssessmentProgressPort.getProgress(assessment.getId());
        if (assessmentProgress.answersCount() != assessmentProgress.questionsCount())
            throw new ValidationException(CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED);

        if (!appAiProperties.isEnabled())
            throw new UnsupportedOperationException(ASSESSMENT_AI_IS_DISABLED);

        var attributeValues = loadAttributeValuePort.load(param.assessmentResult().getId(), param.attributeIds());
        var assessmentTitle = getAssessmentTitle(assessment);
        var maturityLevels = loadMaturityLevelsPort.loadByAssessmentId(param.assessmentResult().getAssessment().getId());
        var attributeIdToFile = attributeValues.stream()
            .collect(toMap(av -> av.getAttribute().getId(),
                attributeValue -> createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels)));
        var attributeToPromptMap = attributeValues.stream()
            .collect(toMap(AttributeValue::getAttribute, attributeValue -> createPrompt(attributeValue.getAttribute().getTitle(),
                attributeValue.getAttribute().getDescription(),
                assessmentTitle,
                attributeIdToFile.get(attributeValue.getAttribute().getId()).text(),
                param.locale().getDisplayLanguage())));

        return attributeToPromptMap.entrySet().stream()
            .map(entry -> {
                var attribute = entry.getKey();
                var aiInsight = callAiPromptPort.call(entry.getValue(), AiResponseDto.class).value();
                var aiInputPath = uploadInputFile(attribute, attributeIdToFile.get(attribute.getId()).stream());
                return toAttributeInsight(param.assessmentResult().getId(), attribute.getId(), aiInsight, aiInputPath);
            })
            .toList();
    }

    @Builder
    public record AttributeInsightsParam(AssessmentResult assessmentResult,
                                         List<Long> attributeIds,
                                         Locale locale) {
    }

    private String getAssessmentTitle(Assessment assessment) {
        return assessment.getShortTitle() != null
            ? assessment.getShortTitle()
            : assessment.getTitle();
    }

    private Prompt createPrompt(String attributeTitle,
                                String attributeDescription,
                                String assessmentTitle,
                                String fileContent,
                                String language) {
        return new PromptTemplate(appAiProperties.getPrompt().getAttributeInsight(),
            Map.of("attributeTitle", attributeTitle,
                "attributeDescription", attributeDescription,
                "assessmentTitle", assessmentTitle,
                "fileContent", fileContent,
                "language", language))
            .create();
    }

    record AiResponseDto(String value) {
    }

    @Nullable
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
}
