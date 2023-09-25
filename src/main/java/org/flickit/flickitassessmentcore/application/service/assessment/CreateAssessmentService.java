package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.application.domain.Subject;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectByAssessmentKitIdPort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.application.domain.Assessment.generateSlugCode;
import static org.flickit.flickitassessmentcore.application.domain.AssessmentColor.getValidId;
import static org.flickit.flickitassessmentcore.application.service.constant.AssessmentConstants.NOT_DELETED_DELETION_TIME;

@Service
@Transactional
@RequiredArgsConstructor
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
            code,
            param.getTitle(),
            param.getAssessmentKitId(),
            getValidId(param.getColorId()),
            param.getSpaceId(),
            creationTime,
            lastModificationTime,
            NOT_DELETED_DELETION_TIME
        );
    }

    private void createAssessmentResult(UUID assessmentId, Long assessmentKitId) {
        LocalDateTime lastModificationTime = LocalDateTime.now();
        CreateAssessmentResultPort.Param param = new CreateAssessmentResultPort.Param(assessmentId, lastModificationTime, false);
        UUID assessmentResultId = createAssessmentResultPort.persist(param);

        List<Subject> subjects = loadSubjectByAssessmentKitIdPort.loadByAssessmentKitId(assessmentKitId);
        List<Long> subjectIds = subjects.stream().map(Subject::getId).toList();
        List<Long> qualityAttributeIds = subjects.stream()
            .map(x -> x.getQualityAttributes().stream().map(QualityAttribute::getId).toList())
            .flatMap(List::stream).toList();
        createSubjectValuePort.persistAll(subjectIds, assessmentResultId);
        createQualityAttributeValuePort.persistAll(qualityAttributeIds, assessmentResultId);
    }
}
