package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.notification.SendNotification;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.domain.notification.CreateAssessmentNotificationCmd;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CountAssessmentsPort;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.CheckKitAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_SPACE_ID_NOT_FOUND;
import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.MANAGER;
import static org.flickit.assessment.core.application.service.constant.AssessmentConstants.NOT_DELETED_DELETION_TIME;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentService implements CreateAssessmentUseCase {

    private static final AssessmentUserRole SPACE_OWNER_ROLE = MANAGER;
    private static final AssessmentUserRole ASSESSMENT_CREATOR_ROLE = MANAGER;
    private static final Double CONFIDENCE_INITIAL_VALUE = 0.0;

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final CheckKitAccessPort checkKitAccessPort;
    private final CreateAssessmentPort createAssessmentPort;
    private final CreateAssessmentResultPort createAssessmentResultPort;
    private final CreateSubjectValuePort createSubjectValuePort;
    private final CreateAttributeValuePort createAttributeValuePort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadSpacePort loadSpacePort;
    private final GrantUserAssessmentRolePort grantUserAssessmentRolePort;
    private final CountAssessmentsPort countAssessmentsPort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final AppSpecProperties appSpecProperties;

    @Override
    @SendNotification
    public Result createAssessment(Param param) {
        var space = loadSpacePort.loadSpace(param.getSpaceId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_SPACE_ID_NOT_FOUND));

        if (!checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentKit = loadAssessmentKitPort.loadAssessmentKit(param.getKitId(), null)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_KIT_ID_NOT_FOUND));

        if (checkKitAccessPort.checkAccess(param.getKitId(), param.getCurrentUserId()).isEmpty())
            throw new ValidationException(CREATE_ASSESSMENT_KIT_NOT_ALLOWED);

        validateSpace(param, space, assessmentKit.getIsPrivate());

        var assessmentLangId = resolveAssessmentLanguage(assessmentKit, param.getLang()).getId();

        UUID id = createAssessmentPort.persist(toParam(param));
        createAssessmentResult(id, assessmentKit.getKitVersion(), assessmentLangId);

        grantAssessmentAccesses(id, space.getOwnerId(), param.getCurrentUserId());

        return new Result(id, new CreateAssessmentNotificationCmd(param.getKitId(), param.getCurrentUserId()));
    }

    private void validateSpace(Param param, Space space, boolean isKitPrivate) {
        if (space.getType() == SpaceType.BASIC) {
            int assessmentsLimit = appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
            int assessmentsCount = countAssessmentsPort.countSpaceAssessments(param.getSpaceId());

            if (assessmentsCount >= assessmentsLimit)
                throw new UpgradeRequiredException(CREATE_ASSESSMENT_BASIC_SPACE_ASSESSMENTS_MAX);

            if (isKitPrivate)
                throw new UpgradeRequiredException(CREATE_ASSESSMENT_BASIC_SPACE_PRIVATE_KIT_NOT_ALLOWED);
        }

        if (space.getType() == SpaceType.PREMIUM &&
            space.getSubscriptionExpiry() != null &&
            LocalDateTime.now().isAfter(space.getSubscriptionExpiry())) {
            throw new UpgradeRequiredException(CREATE_ASSESSMENT_PREMIUM_SPACE_EXPIRED);
        }
    }

    private KitLanguage resolveAssessmentLanguage(AssessmentKit kit, String langParam) {
        KitLanguage assessmentLanguage = langParam == null
            ? kit.getLanguage()
            : KitLanguage.valueOf(langParam);

        if (!kit.getSupportedLanguages().contains(assessmentLanguage))
            throw new ValidationException(CREATE_ASSESSMENT_LANGUAGE_NOT_SUPPORTED);

        return assessmentLanguage;
    }

    private CreateAssessmentPort.Param toParam(Param param) {
        String code = generateSlugCode(param.getTitle());
        LocalDateTime creationTime = LocalDateTime.now();
        return new CreateAssessmentPort.Param(
            code,
            param.getTitle(),
            param.getShortTitle(),
            param.getKitId(),
            param.getSpaceId(),
            creationTime,
            NOT_DELETED_DELETION_TIME,
            false,
            param.getCurrentUserId());
    }

    private void createAssessmentResult(UUID assessmentId, Long kitVersionId, int langId) {
        LocalDateTime lastModificationTime = LocalDateTime.now();
        MaturityLevel maturityLevel = resolveInitialMaturityLevel(kitVersionId);
        CreateAssessmentResultPort.Param param = new CreateAssessmentResultPort.Param(assessmentId, kitVersionId,
            maturityLevel.getId(), CONFIDENCE_INITIAL_VALUE, lastModificationTime, false, false, langId);
        UUID assessmentResultId = createAssessmentResultPort.persist(param);

        List<Subject> subjects = loadSubjectsPort.loadByKitVersionIdWithAttributes(kitVersionId);
        List<Long> subjectIds = subjects.stream().map(Subject::getId).toList();
        Set<Long> attributeIds = subjects.stream()
            .map(x -> x.getAttributes().stream().map(Attribute::getId).toList())
            .flatMap(List::stream).collect(Collectors.toSet());
        createSubjectValuePort.persistAll(subjectIds, assessmentResultId);
        createAttributeValuePort.persistAll(attributeIds, assessmentResultId);
    }

    private MaturityLevel resolveInitialMaturityLevel(Long kitVersionId) {
        return loadMaturityLevelsPort.loadAll(kitVersionId).stream()
            .sorted(Comparator.comparingInt(MaturityLevel::getValue))
            .toList()
            .getFirst();
    }

    private void grantAssessmentAccesses(UUID assessmentId, UUID spaceOwnerId, UUID currentUserId) {
        if (!Objects.equals(spaceOwnerId, currentUserId))
            grantUserAssessmentRolePort.persist(assessmentId, spaceOwnerId, SPACE_OWNER_ROLE.getId());
        grantUserAssessmentRolePort.persist(assessmentId, currentUserId, ASSESSMENT_CREATOR_ROLE.getId());
    }
}
