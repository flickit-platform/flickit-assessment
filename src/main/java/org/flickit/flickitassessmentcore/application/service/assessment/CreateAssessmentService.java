package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectByAssessmentKitIdPort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;
import org.flickit.flickitassessmentcore.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.domain.Subject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.domain.Assessment.generateSlugCode;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateAssessmentService implements CreateAssessmentUseCase {

    private final CreateAssessmentPort createAssessmentPort;
    private final CreateAssessmentResultPort createAssessmentResultPort;
    private final CreateSubjectValuePort createSubjectValuePort;
    private final CreateQualityAttributeValuePort createQualityAttributeValuePort;
    private final LoadSubjectByAssessmentKitIdPort loadSubjectByAssessmentKitIdPort;

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

    private int getValidColorId(Integer colorId) {
        if (colorId == null || !AssessmentColor.isValidId(colorId))
            return AssessmentColor.getDefault().getId();
        return colorId;
    }

    private void createAssessmentResult(UUID assessmentId, Long assessmentKitId) {
        CreateAssessmentResultPort.Param createAssessmentResultParam = new CreateAssessmentResultPort.Param(assessmentId, false);
        UUID assessmentResultId = createAssessmentResultPort.persist(createAssessmentResultParam);


        List<Subject> subjects = loadSubjectByAssessmentKitIdPort.loadByAssessmentKitId(assessmentKitId);
        List<Long> subjectIds = subjects.stream().map(Subject::getId).toList();
        List<Long> qualityAttributeIds = subjects.stream()
            .map(x -> x.getQualityAttributes().stream().map(QualityAttribute::getId).toList())
            .flatMap(List::stream).toList();
        createSubjectValuePort.persistAll(subjectIds, assessmentResultId);
        createQualityAttributeValuePort.persistAll(qualityAttributeIds, assessmentResultId);
    }
}
