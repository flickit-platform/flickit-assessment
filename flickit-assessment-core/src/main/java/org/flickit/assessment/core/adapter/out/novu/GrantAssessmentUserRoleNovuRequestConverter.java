package org.flickit.assessment.core.adapter.out.novu;

import org.flickit.assessment.common.adapter.out.novu.AbstractNovuRequestConverter;
import org.flickit.assessment.common.application.domain.notification.Tenant;
import org.flickit.assessment.core.application.service.assessmentuserrole.notification.GrantAssessmentUserRoleNotificationPayload;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.GRANT_USER_ASSESSMENT_ROLE;

@Component
public class GrantAssessmentUserRoleNovuRequestConverter extends AbstractNovuRequestConverter {

    public GrantAssessmentUserRoleNovuRequestConverter(Tenant tenant) {
        super(tenant);
    }

    @Override
    protected String getEventName() {
        return GRANT_USER_ASSESSMENT_ROLE.getCode();
    }

    @Override
    public Class<GrantAssessmentUserRoleNotificationPayload> payloadClass() {
        return GrantAssessmentUserRoleNotificationPayload.class;
    }
}
