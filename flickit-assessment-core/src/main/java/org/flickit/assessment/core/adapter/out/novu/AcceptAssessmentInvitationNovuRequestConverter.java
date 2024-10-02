package org.flickit.assessment.core.adapter.out.novu;

import org.flickit.assessment.common.adapter.out.novu.AbstractNovuRequestConverter;
import org.flickit.assessment.core.application.service.assessmentinvite.notification.AcceptAssessmentInvitationNotificationPayload;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.ACCEPT_ASSESSMENT_INVITATION;

@Component
public class AcceptAssessmentInvitationNovuRequestConverter extends AbstractNovuRequestConverter {

    @Override
    protected String getEventName() {
        return ACCEPT_ASSESSMENT_INVITATION.getCode();
    }

    @Override
    public Class<AcceptAssessmentInvitationNotificationPayload> payloadClass() {
        return AcceptAssessmentInvitationNotificationPayload.class;
    }
}
