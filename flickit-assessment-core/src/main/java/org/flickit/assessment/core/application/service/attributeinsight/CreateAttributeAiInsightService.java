package org.flickit.assessment.core.application.service.attributeinsight;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.attributeinsight.CreateAttributeAiInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeScoresFilePort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.UpdateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoresFilePort;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ATTRIBUTE_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_AI_IS_DISABLED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAttributeAiInsightService implements CreateAttributeAiInsightUseCase {

    private static final String AI_INPUT_FILE_EXTENSION = ".xlsx";

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAttributeValuePort loadAttributeValuePort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadAttributePort loadAttributePort;
    private final LoadAttributeInsightPort loadAttributeInsightPort;
    private final AppAiProperties appAiProperties;
    private final CreateAttributeInsightPort createAttributeInsightPort;
    private final CreateAttributeScoresFilePort createAttributeScoresFilePort;
    private final UploadAttributeScoresFilePort uploadAttributeScoresFilePort;
    private final UpdateAttributeInsightPort updateAttributeInsightPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final OpenAiProperties openAiProperties;
    private final CallAiPromptPort callAiPromptPort;

    @SneakyThrows
    @Override
    public Result createAiInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var progress = getAssessmentProgressPort.getProgress(param.getAssessmentId());
        if (progress.answersCount() != progress.questionsCount())
            throw new ValidationException(CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        validateAssessmentResultPort.validate(param.getAssessmentId());

        AttributeValue attributeValue = loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId());
        List<MaturityLevel> maturityLevels = loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId());

        var attribute = loadAttributePort.load(param.getAttributeId(), assessmentResult.getKitVersionId());
        var attributeInsight = loadAttributeInsightPort.load(assessmentResult.getId(), attribute.getId());
        var assessmentTitle = assessmentResult.getAssessment().getShortTitle() != null ? assessmentResult.getAssessment().getShortTitle() : assessmentResult.getAssessment().getTitle();

        return attributeInsight.map(insight -> handleExistingInsight(insight, assessmentResult, assessmentTitle, attribute, attributeValue, maturityLevels))
            .orElseGet(() -> handleNewInsight(attribute, assessmentResult, assessmentTitle, attributeValue, maturityLevels));
    }

    @SneakyThrows
    private Result handleExistingInsight(AttributeInsight attributeInsight, AssessmentResult assessmentResult, String assessmentTitle,
                                         Attribute attribute, AttributeValue attributeValue, List<MaturityLevel> maturityLevels) {
        if (assessmentResult.getLastCalculationTime().isBefore(attributeInsight.getAiInsightTime()))
            return new Result(attributeInsight.getAiInsight());

        if (!appAiProperties.isEnabled())
            throw new UnsupportedOperationException(ASSESSMENT_AI_IS_DISABLED);

        var file = createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels);
        String aiInputPath = uploadInputFile(attribute, file.stream());
        var prompt = openAiProperties.createAttributeAiInsightPrompt(attribute.getTitle(), attribute.getDescription(), assessmentTitle, file.text());
        String aiInsight = callAiPromptPort.call(prompt);
        updateAttributeInsightPort.updateAiInsight(toAttributeInsight(assessmentResult.getId(), attribute.getId(), aiInsight, aiInputPath));
        return new Result(aiInsight);
    }

    @SneakyThrows
    private Result handleNewInsight(Attribute attribute, AssessmentResult assessmentResult, String assessmentTitle,
                                    AttributeValue attributeValue, List<MaturityLevel> maturityLevels) {
        if (!appAiProperties.isEnabled())
            throw new UnsupportedOperationException(ASSESSMENT_AI_IS_DISABLED);

        var file = createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels);
        String aiInputPath = uploadInputFile(attribute, file.stream());
        var prompt = openAiProperties.createAttributeAiInsightPrompt(attribute.getTitle(), attribute.getDescription(), assessmentTitle, file.text());
        String aiInsight = callAiPromptPort.call(prompt);
        createAttributeInsightPort.persist(toAttributeInsight(assessmentResult.getId(), attribute.getId(), aiInsight, aiInputPath));
        return new Result(aiInsight);
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

    private AttributeInsight toAttributeInsight(UUID assessmentResultId, long attributeId, String aiInsight, String aiInputPath) {
        return new AttributeInsight(assessmentResultId,
            attributeId,
            aiInsight,
            null,
            LocalDateTime.now(),
            null,
            aiInputPath,
            false);
    }
}
