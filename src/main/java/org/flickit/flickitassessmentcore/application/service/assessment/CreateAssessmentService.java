package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectIdsAndQualityAttributeIdsPort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateAssessmentService implements CreateAssessmentUseCase {

    private final CreateAssessmentPort createAssessmentPort;
    private final CreateAssessmentResultPort createAssessmentResultPort;
    private final CreateSubjectValuePort createSubjectValuePort;
    private final CreateQualityAttributeValuePort createQualityAttributeValuePort;
    private final LoadSubjectIdsAndQualityAttributeIdsPort loadSubjectIdsAndQualityAttributeIdsPort;

    @Override
    public Result createAssessment(Param param) {
        CreateAssessmentPort.Param portParam = toParam(param);
        UUID id = createAssessmentPort.persist(portParam);
        createAssessmentResult(id, portParam.assessmentKitId());
        return new Result(id);
    }

    private CreateAssessmentPort.Param toParam(Param param) {
        String code = generateSlugCode(param.getTitle());
        LocalDateTime creationTime = LocalDateTime.now();
        LocalDateTime lastModificationTime = LocalDateTime.now();

        return new CreateAssessmentPort.Param(
            param.getTitle(),
            param.getAssessmentKitId(),
            getValidColorId(param.getColorId()),
            param.getSpaceId(),
            code,
            creationTime,
            lastModificationTime
        );
    }

    private String generateSlugCode(String title) {
        return title
            .toLowerCase()
            .strip()
            .replaceAll("\\s+", "-");
    }

    private int getValidColorId(Integer colorId) {
        if (colorId == null || !AssessmentColor.isValidId(colorId))
            return AssessmentColor.getDefault().getId();
        return colorId;
    }

    private void createAssessmentResult(UUID assessmentId, Long assessmentKitId) {
        LocalDateTime lastModificationTime = LocalDateTime.now();
        CreateAssessmentResultPort.Param param = new CreateAssessmentResultPort.Param(assessmentId, lastModificationTime, false);
        UUID assessmentResultId = createAssessmentResultPort.persist(param);

        LoadSubjectIdsAndQualityAttributeIdsPort.ResponseParam responseParams =
            loadSubjectIdsAndQualityAttributeIdsPort.loadByAssessmentKitId(assessmentKitId);
        createSubjectValuePort.persistAll(responseParams.subjectIds(), assessmentResultId);
        createQualityAttributeValuePort.persistAll(responseParams.qualityAttributeIds(), assessmentResultId);
    }
}
