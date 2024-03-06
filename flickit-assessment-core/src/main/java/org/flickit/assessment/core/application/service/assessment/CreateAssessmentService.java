package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitVersionIdPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.application.domain.Assessment.generateSlugCode;
import static org.flickit.assessment.core.application.domain.AssessmentColor.getValidId;
import static org.flickit.assessment.core.application.service.constant.AssessmentConstants.NOT_DELETED_DELETION_TIME;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentService implements CreateAssessmentUseCase {

    private final CreateAssessmentPort createAssessmentPort;
    private final CreateAssessmentResultPort createAssessmentResultPort;
    private final CreateSubjectValuePort createSubjectValuePort;
    private final CreateQualityAttributeValuePort createQualityAttributeValuePort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadAssessmentKitVersionIdPort loadKitVersionIdPort;

    @Override
    public Result createAssessment(Param param) {
        CreateAssessmentPort.Param portParam = toParam(param);
        UUID id = createAssessmentPort.persist(portParam);
        createAssessmentResult(id, loadKitVersionIdPort.loadVersionId(param.getAssessmentKitId()));
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
            NOT_DELETED_DELETION_TIME,
            false,
            param.getCreatedBy()
        );
    }

    private void createAssessmentResult(UUID assessmentId, Long kitVersionId) {
        LocalDateTime lastModificationTime = LocalDateTime.now();
        CreateAssessmentResultPort.Param param = new CreateAssessmentResultPort.Param(assessmentId, kitVersionId,
            lastModificationTime, false, false);
        UUID assessmentResultId = createAssessmentResultPort.persist(param);

        List<Subject> subjects = loadSubjectsPort.loadByKitVersionIdWithAttributes(kitVersionId);
        List<Long> subjectIds = subjects.stream().map(Subject::getId).toList();
        List<Long> qualityAttributeIds = subjects.stream()
            .map(x -> x.getQualityAttributes().stream().map(QualityAttribute::getId).toList())
            .flatMap(List::stream).toList();
        createSubjectValuePort.persistAll(subjectIds, assessmentResultId);
        createQualityAttributeValuePort.persistAll(qualityAttributeIds, assessmentResultId);
    }
}
