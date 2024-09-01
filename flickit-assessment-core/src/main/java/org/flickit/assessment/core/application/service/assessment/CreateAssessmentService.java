package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.notification.SendNotification;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.CheckKitAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitVersionIdPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.core.application.service.assessment.notification.CreateAssessmentNotificationCmd;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.Assessment.generateSlugCode;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.MANAGER;
import static org.flickit.assessment.core.application.service.constant.AssessmentConstants.NOT_DELETED_DELETION_TIME;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_KIT_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentService implements CreateAssessmentUseCase {

    private static final AssessmentUserRole SPACE_OWNER_ROLE = MANAGER;
    private static final AssessmentUserRole ASSESSMENT_CREATOR_ROLE = MANAGER;

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final CheckKitAccessPort checkKitAccessPort;
    private final CreateAssessmentPort createAssessmentPort;
    private final LoadAssessmentKitVersionIdPort loadKitVersionIdPort;
    private final CreateAssessmentResultPort createAssessmentResultPort;
    private final CreateSubjectValuePort createSubjectValuePort;
    private final CreateAttributeValuePort createAttributeValuePort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadSpaceOwnerPort loadSpaceOwnerPort;
    private final GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Override
    @SendNotification
    public Result createAssessment(Param param) {
        if (!checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId()).isEmpty())
            throw new ValidationException(CREATE_ASSESSMENT_KIT_NOT_ALLOWED);

        UUID id = createAssessmentPort.persist(toParam(param));
        createAssessmentResult(id, loadKitVersionIdPort.loadVersionId(param.getKitId()));

        grantAssessmentAccesses(param, id);

        return new Result(id, new CreateAssessmentNotificationCmd(param.getKitId()));
    }

    private CreateAssessmentPort.Param toParam(Param param) {
        String code = generateSlugCode(param.getTitle());
        LocalDateTime creationTime = LocalDateTime.now();
        return new CreateAssessmentPort.Param(
            code,
            param.getTitle(),
            param.getKitId(),
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
            .map(x -> x.getAttributes().stream().map(Attribute::getId).toList())
            .flatMap(List::stream).toList();
        createSubjectValuePort.persistAll(subjectIds, assessmentResultId);
        createAttributeValuePort.persistAll(attributeIds, assessmentResultId);
    }

    private void grantAssessmentAccesses(Param param, UUID assessmentId) {
        var spaceOwnerId = loadSpaceOwnerPort.loadOwnerId(assessmentId);
        if (!Objects.equals(param.getCurrentUserId(), spaceOwnerId))
            grantUserAssessmentRolePort.persist(assessmentId, spaceOwnerId, SPACE_OWNER_ROLE.getId());
        grantUserAssessmentRolePort.persist(assessmentId, param.getCurrentUserId(), ASSESSMENT_CREATOR_ROLE.getId());
    }
}
