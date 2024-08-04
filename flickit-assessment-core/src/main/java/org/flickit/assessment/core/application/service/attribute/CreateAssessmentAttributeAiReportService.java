package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.attribute.CreateAssessmentAttributeAiReportUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAssessmentAttributeAiPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.UpdateAttributeInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentAttributeAiReportService implements CreateAssessmentAttributeAiReportUseCase {

    private final LoadAttributePort loadAttributePort;
    private final GetAssessmentPort getAssessmentPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeInsightPort loadAttributeInsightPort;
    private final CreateAttributeInsightPort createAttributeInsightPort;
    private final UpdateAttributeInsightPort updateAttributeInsightPort;
    private final CreateAssessmentAttributeAiPort createAssessmentAttributeAiPort;

    @SneakyThrows
    @Override
    public Result createAttributeAiReport(Param param) {
        var assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        if (!assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessment.getId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND));

        var attribute = loadAttributePort.load(param.getAttributeId(), assessmentResult.getKitVersionId());

        var attributeInsight = loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), attribute.getId());
        if (attributeInsight.isEmpty()) {
            try (var stream = downloadFile(param.getFileLink())) {
                var aiInsight = createAssessmentAttributeAiPort.createReport(stream, attribute);
                createAttributeInsightPort.persist(toAttributeInsightCreateParam(assessmentResult.getId(), attribute.getId(),
                    attribute.getTitle(), aiInsight, LocalDateTime.now(), param.getFileLink()));
                return new Result(aiInsight);
            }
        }

        boolean isCalculatedValid = assessmentResult.getIsCalculateValid() != null && assessmentResult.getIsCalculateValid();
        if (isCalculatedValid) {
            if (assessmentResult.getLastCalculationTime().isBefore(attributeInsight.get().getAiInsightTime()))
                return new Result(attributeInsight.get().getAiInsight());

            try (var stream = downloadFile(param.getFileLink())) {
                var newAiInsight = createAssessmentAttributeAiPort.createReport(stream, attribute);
                updateAttributeInsightPort.update(toAttributeInsightUpdateParam(assessmentResult.getId(), attribute.getId(), attribute.getTitle(),
                    newAiInsight, attributeInsight.get().getAssessorInsight(), LocalDateTime.now(), attributeInsight.get().getAssessorInsightTime(), param.getFileLink()));
                return new Result(newAiInsight);
            }
        }

        throw new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID);
    }

    @SneakyThrows
    InputStream downloadFile(String fileLink) {
        URL fileUrl = new URL(fileLink);

        try (ReadableByteChannel readableByteChannel = Channels.newChannel(fileUrl.openStream());
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (readableByteChannel.read(buffer) > 0) {
                buffer.flip();
                byteArrayOutputStream.write(buffer.array(), 0, buffer.limit());
                buffer.clear();
            }
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            throw new ResourceNotFoundException(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_FILE_NOT_FOUND);
        }
    }

    private CreateAttributeInsightPort.Param toAttributeInsightCreateParam(UUID assessmentResultId, long attributeId, String attributeTitle, String aiInsight,
                                                                           LocalDateTime aiInsightTime, String fileLink) {
        return new CreateAttributeInsightPort.Param(assessmentResultId,
            attributeId,
            attributeTitle,
            aiInsight,
            null,
            aiInsightTime,
            null,
            fileLink);
    }

    private UpdateAttributeInsightPort.Param toAttributeInsightUpdateParam(UUID assessmentResultId, long attributeId, String attributeTitle, String newAiInsight,
                                                                           String assessorInsight, LocalDateTime aiInsightTime, LocalDateTime assessorInsightTime, String fileLink) {
        return new UpdateAttributeInsightPort.Param(assessmentResultId,
            attributeId,
            attributeTitle,
            newAiInsight,
            assessorInsight,
            aiInsightTime,
            assessorInsightTime,
            fileLink);
    }
}
