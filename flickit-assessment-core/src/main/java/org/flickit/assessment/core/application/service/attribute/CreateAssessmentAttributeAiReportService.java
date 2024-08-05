package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.in.attribute.CreateAssessmentAttributeAiReportUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAssessmentAttributeAiPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.UpdateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributevalue.GenerateAttributeValueReportFilePort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentAttributeAiReportService implements CreateAssessmentAttributeAiReportUseCase {

    private final OpenAiProperties openAiProperties;
    private final LoadAttributePort loadAttributePort;
    private final GetAssessmentPort getAssessmentPort;
    private final LoadAttributeValuePort loadAttributeValuePort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeInsightPort loadAttributeInsightPort;
    private final CreateAttributeInsightPort createAttributeInsightPort;
    private final UpdateAttributeInsightPort updateAttributeInsightPort;
    private final CreateAssessmentAttributeAiPort createAssessmentAttributeAiPort;
    private final GenerateAttributeValueReportFilePort generateAttributeValueReportFilePort;

    @SneakyThrows
    @Override
    public Result createAttributeAiReport(Param param) {
        var assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        if (!assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessment.getId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND));

        boolean isCalculatedValid = assessmentResult.getIsCalculateValid() != null && assessmentResult.getIsCalculateValid();
        if (!isCalculatedValid)
            throw new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID);

        AttributeValue attributeValue = loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId());
        List<MaturityLevel> maturityLevels = loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId());

        var attribute = loadAttributePort.load(param.getAttributeId(), assessmentResult.getKitVersionId());
        var attributeInsight = loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), attribute.getId());
        if (attributeInsight.isEmpty()) {
            if (!openAiProperties.isEnabled())
                return new Result(MessageBundle.message(ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED, attribute.getTitle()));
            try (var stream = generateAttributeValueReportFilePort.generateReport(attributeValue, maturityLevels)) {
                var aiInsight = createAssessmentAttributeAiPort.createReport(stream, attribute);
                createAttributeInsightPort.persist(new AttributeInsight(assessmentResult.getId(), attribute.getId(),
                    aiInsight, null, LocalDateTime.now(), null, param.getFileLink()));
                return new Result(aiInsight);
            }
        }

        if (assessmentResult.getLastCalculationTime().isBefore(attributeInsight.get().getAiInsightTime()))
            return new Result(attributeInsight.get().getAiInsight());

        try (var stream = generateAttributeValueReportFilePort.generateReport(attributeValue, maturityLevels)) {
            if (!openAiProperties.isEnabled())
                return new Result(attributeInsight.get().getAiInsight());
            var newAiInsight = createAssessmentAttributeAiPort.createReport(stream, attribute);
            updateAttributeInsightPort.update(new AttributeInsight(assessmentResult.getId(), attribute.getId(), newAiInsight,
                attributeInsight.get().getAssessorInsight(), LocalDateTime.now(), attributeInsight.get().getAssessorInsightTime(), param.getFileLink()));
            return new Result(newAiInsight);
        }
    }
}
