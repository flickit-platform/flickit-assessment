package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.SendEmailPort;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.port.in.assessment.GrantAccessToReportUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.CreateAssessmentInvitePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.space.CreateSpaceInvitePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_ACCESS_TO_REPORT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_GRAPHICAL_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.INVITE_TO_REGISTER_EMAIL_BODY;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.REPORT_VIEWER;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GrantAccessToReportService implements GrantAccessToReportUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentPort loadAssessmentPort;
    private final LoadUserPort loadUserPort;
    private final GrantUserAssessmentRolePort grantUserAssessmentRolePort;
    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;
    private final CreateSpaceInvitePort createSpaceInvitePort;
    private final CreateAssessmentInvitePort createAssessmentInvitePort;
    private final AppSpecProperties appSpecProperties;
    private final SendEmailPort sendEmailPort;
    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final CreateAssessmentSpaceUserAccessPort createAssessmentSpaceUserAccessPort;

    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    @Override
    public void grantAccessToReport(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var creationTime = LocalDateTime.now();
        Assessment assessment = loadAssessmentPort.getAssessmentById(param.getAssessmentId()).orElseThrow();
        var userOptional = loadUserPort.loadByEmail(param.getEmail());

        if (userOptional.isPresent()) {
            var user = userOptional.get();
            if (!checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), user.getId())) {
                var createAssessmentParam = new CreateAssessmentSpaceUserAccessPort.Param(
                    assessment.getId(), user.getId(), param.getCurrentUserId(), creationTime);
                createAssessmentSpaceUserAccessPort.persist(createAssessmentParam);
            }

            var roleOptional = loadUserRoleForAssessmentPort.load(param.getAssessmentId(), user.getId());
            if (roleOptional.isPresent()) {
                if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), user.getId(), VIEW_GRAPHICAL_REPORT))
                    throw new ValidationException(GRANT_ACCESS_TO_REPORT_NOT_ALLOWED_CONTACT_ASSESSMENT_MANAGER);
                else
                    throw new ResourceAlreadyExistsException(GRANT_ACCESS_TO_REPORT_USER_ALREADY_GRANTED);
            } else
                grantUserAssessmentRolePort.persist(param.getAssessmentId(), user.getId(), REPORT_VIEWER.getId());

        } else {
            var expirationTime = creationTime.plusDays(EXPIRY_DURATION.toDays());
            createSpaceInvitePort.persist(toCreateSpaceInviteParam(assessment.getSpace().getId(), param, expirationTime, creationTime));
            createAssessmentInvitePort.persist(toCreateAssessmentInviteParam(param, expirationTime, creationTime));
            sendInviteEmail(param.getEmail());
        }
    }

    CreateSpaceInvitePort.Param toCreateSpaceInviteParam(long spaceId,
                                                         Param param,
                                                         LocalDateTime expirationTime,
                                                         LocalDateTime creationTime) {
        return new CreateSpaceInvitePort.Param(spaceId,
            param.getEmail(),
            expirationTime,
            creationTime,
            param.getCurrentUserId());
    }

    CreateAssessmentInvitePort.Param toCreateAssessmentInviteParam(Param param,
                                                                   LocalDateTime expirationTime,
                                                                   LocalDateTime creationTime) {
        return new CreateAssessmentInvitePort.Param(param.getAssessmentId(),
            param.getEmail(),
            REPORT_VIEWER.getId(),
            expirationTime,
            creationTime,
            param.getCurrentUserId());
    }

    private void sendInviteEmail(String sendTo) {
        String subject = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_SUBJECT, appSpecProperties.getName());
        String body = generateEmailBody();
        log.debug("Sending invite email to [{}]", sendTo);
        sendEmailPort.send(sendTo, subject, body);
    }

    private String generateEmailBody() {
        if (appSpecProperties.getSupportEmail() == null || appSpecProperties.getSupportEmail().isBlank())
            return MessageBundle.message(INVITE_TO_REGISTER_EMAIL_BODY_WITHOUT_SUPPORT_EMAIL,
                appSpecProperties.getHost(),
                appSpecProperties.getName());
        return MessageBundle.message(INVITE_TO_REGISTER_EMAIL_BODY,
            appSpecProperties.getHost(),
            appSpecProperties.getName(),
            appSpecProperties.getSupportEmail());
    }
}
