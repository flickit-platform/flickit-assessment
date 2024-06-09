package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.CheckKitAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitVersionIdPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.Assessment.generateSlugCode;
import static org.flickit.assessment.core.application.domain.AssessmentColor.getValidId;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.MANAGER;
import static org.flickit.assessment.core.application.service.constant.AssessmentConstants.NOT_DELETED_DELETION_TIME;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_KIT_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentService implements CreateAssessmentUseCase {

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final CheckKitAccessPort checkKitAccessPort;
    private final CreateAssessmentPort createAssessmentPort;
    private final LoadAssessmentKitVersionIdPort loadKitVersionIdPort;
    private final CreateAssessmentResultPort createAssessmentResultPort;
    private final CreateSubjectValuePort createSubjectValuePort;
    private final CreateQualityAttributeValuePort createQualityAttributeValuePort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Override
    public Result createAssessment(Param param) {
        if (!checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId()).isEmpty())
            throw new ValidationException(CREATE_ASSESSMENT_KIT_NOT_ALLOWED);

        UUID id = createAssessmentPort.persist(toParam(param));
        createAssessmentResult(id, loadKitVersionIdPort.loadVersionId(param.getKitId()));

        grantUserAssessmentRolePort.persist(id, param.getCurrentUserId(), MANAGER.getId());

        return new Result(id);
    }

    private CreateAssessmentPort.Param toParam(Param param) {
        String code = generateSlugCode(param.getTitle());
        LocalDateTime creationTime = LocalDateTime.now();
        return new CreateAssessmentPort.Param(
            code,
            param.getTitle(),
            param.getKitId(),
            getValidId(param.getColorId()),
            param.getSpaceId(),
            creationTime,
            NOT_DELETED_DELETION_TIME,
            false,
            param.getCurrentUserId());
    }

    private void createAssessmentResult(UUID assessmentId, Long kitVersionId) {
        LocalDateTime lastModificationTime = LocalDateTime.now();
        CreateAssessmentResultPort.Param param = new CreateAssessmentResultPort.Param(assessmentId, kitVersionId,
            lastModificationTime, false, false);
        UUID assessmentResultId = createAssessmentResultPort.persist(param);

        List<Subject> subjects = loadSubjectsPort.loadByKitVersionIdWithAttributes(kitVersionId);
        List<Long> subjectIds = subjects.stream().map(Subject::getId).toList();
        List<Long> attributeIds = subjects.stream()
            .map(x -> x.getQualityAttributes().stream().map(QualityAttribute::getId).toList())
            .flatMap(List::stream).toList();
        createSubjectValuePort.persistAll(subjectIds, assessmentResultId);
        createQualityAttributeValuePort.persistAll(attributeIds, assessmentResultId);
    }
}
