package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubject.LoadAssessmentSubjectIdsByAssessmentKitPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubjectvalue.CreateAssessmentSubjectValuePort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeIdsByAssessmentSubjectPort;
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
    private final LoadAssessmentSubjectIdsByAssessmentKitPort loadAssessmentSubjectIdsPort;
    private final CreateAssessmentSubjectValuePort createAssessmentSubjectValuePort;
    private final LoadQualityAttributeIdsByAssessmentSubjectPort loadQualityAttributeIdsPort;
    private final org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort createQualityAttributeValuePort;

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

        createAssessmentSubjectValues(assessmentKitId, assessmentResultId);
        createQualityAttributeValues(assessmentKitId, assessmentResultId);
    }

    private void createAssessmentSubjectValues(Long assessmentKitId, UUID assessmentResultId) {
        List<Long> assessmentSubjectIds = loadAssessmentSubjectIdsPort.loadIdsByAssessmentKitId(assessmentKitId);

        List<CreateAssessmentSubjectValuePort.Param> params = assessmentSubjectIds.stream()
            .map(assessmentSubjectId -> new CreateAssessmentSubjectValuePort.Param(assessmentSubjectId)).toList();
        createAssessmentSubjectValuePort.persistAllWithAssessmentResultId(params, assessmentResultId);
    }

    private void createQualityAttributeValues(Long assessmentKitId, UUID assessmentResultId) {
        List<Long> assessmentSubjectIds = loadAssessmentSubjectIdsPort.loadIdsByAssessmentKitId(assessmentKitId);

        for (Long assessmentSubjectId : assessmentSubjectIds) {
            List<Long> qualityAttributeIds = loadQualityAttributeIdsPort.loadIdsByAssessmentSubjectId(assessmentSubjectId);

            List<org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort.Param> params = qualityAttributeIds.stream()
                .map(qualityAttributeId -> new org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort.Param(qualityAttributeId)).toList();
            createQualityAttributeValuePort.persistAllWithAssessmentResultId(params, assessmentResultId);
        }
    }
}
