package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubject.LoadAssessmentSubjectIdsAndQualityAttributeIdsPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubjectvalue.CreateAssessmentSubjectValuePort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateAssessmentService implements CreateAssessmentUseCase {

    private final CreateAssessmentPort createAssessmentPort;
    private final CreateAssessmentResultPort createAssessmentResultPort;
    private final CreateAssessmentSubjectValuePort createAssessmentSubjectValuePort;
    private final CreateQualityAttributeValuePort createQualityAttributeValuePort;
    private final LoadAssessmentSubjectIdsAndQualityAttributeIdsPort loadAssessmentSubjectIdsAndQualityAttributeIdsPort;

    @Override
    public UUID createAssessment(CreateAssessmentCommand command) {
        CreateAssessmentPort.Param param = toParam(command);
        UUID id = createAssessmentPort.persist(param);
        createAssessmentResult(id, param.assessmentKitId());
        return id;
    }

    private CreateAssessmentPort.Param toParam(CreateAssessmentCommand command) {
        String code = generateSlugCode(command.getTitle());
        LocalDateTime creationTime = LocalDateTime.now();
        LocalDateTime lastModificationTime = LocalDateTime.now();

        return new CreateAssessmentPort.Param(
            command.getTitle(),
            command.getAssessmentKitId(),
            getValidColorId(command.getColorId()),
            command.getSpaceId(),
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
        CreateAssessmentResultPort.Param createAssessmentResultParam = new CreateAssessmentResultPort.Param(assessmentId, false);
        UUID assessmentResultId = createAssessmentResultPort.persist(createAssessmentResultParam);

        LoadAssessmentSubjectIdsAndQualityAttributeIdsPort.ResponseParam responseParams =
            loadAssessmentSubjectIdsAndQualityAttributeIdsPort.loadByAssessmentKitId(assessmentKitId);
        createAssessmentSubjectValues(responseParams.assessmentSubjectIds(), assessmentResultId);
        createQualityAttributeValues(responseParams.qualityAttributeIds(), assessmentResultId);
    }

    private void createAssessmentSubjectValues(List<Long> assessmentSubjectIds, UUID assessmentResultId) {
        List<CreateAssessmentSubjectValuePort.Param> params = assessmentSubjectIds.stream()
            .map(CreateAssessmentSubjectValuePort.Param::new).toList();
        createAssessmentSubjectValuePort.persistAllWithAssessmentResultId(params, assessmentResultId);
    }

    private void createQualityAttributeValues(List<Long> qualityAttributeIds, UUID assessmentResultId) {
        List<CreateQualityAttributeValuePort.Param> params = qualityAttributeIds.stream()
            .map(CreateQualityAttributeValuePort.Param::new).toList();
        createQualityAttributeValuePort.persistAllWithAssessmentResultId(params, assessmentResultId);
    }
}
